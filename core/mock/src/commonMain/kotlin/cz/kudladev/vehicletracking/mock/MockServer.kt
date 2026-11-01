package cz.kudladev.vehicletracking.mock

import cz.kudladev.vehicletracking.network.*
import io.ktor.client.engine.mock.*
import io.ktor.client.request.HttpRequestData
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.utils.io.*
import kotlinx.coroutines.launch
import kotlinx.io.readByteArray
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.*
import kotlinx.serialization.json.*
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/** Each instance is a deterministic, isolated server. No native engine or URL fetcher exists. */
class MockServer(initialScenario: Scenario = Scenario.NORMAL) {
    enum class Scenario { NORMAL, EMPTY, BAD_AUTH, SERVER_ERROR, TIMEOUT, LOST_UPLOAD_RESPONSE }
    companion object {
        const val ORIGIN = "https://mock.motopark.invalid"
        const val PASSWORD = "Fixture-only-1!"
        const val USER = "00000000-0000-0000-0000-000000000001"
        const val ADMIN = "00000000-0000-0000-0000-000000000004"
        const val VEHICLE = "2147483648"
        val api = ApiConfig(ORIGIN, requireHttps = true)
    }
    private val mutex = Mutex()
    var scenario = initialScenario
        private set
    var now = Instant.parse("2026-11-02T08:00:00Z")
        private set
    private var sequence = 100
    private var imageSequence = 200
    private var tokenSequence = 1
    private var epoch = 0
    private val tokens = mutableMapOf<String, String>()
    private val refresh = mutableMapOf<String, String>()
    private val trackings = linkedMapOf<String, JsonObject>()
    private val photos = mutableMapOf<String, ByteArray>()
    private data class Receipt(val fingerprint: String, val image: String)
    private val receipts = mutableMapOf<String, Receipt>()
    private val users = mutableMapOf<String, JsonObject>()
    private lateinit var vehicle: JsonObject
    init { seed() }

    /** Call with no in-flight application jobs; hosts reset by starting a fresh process/sandbox. */
    suspend fun reset(scenario: Scenario = Scenario.NORMAL, time: Instant = Instant.parse("2026-11-02T08:00:00Z")) = mutex.withLock {
        epoch++ // Old credentials never become valid again even though fixture/entity IDs repeat.
        sequence = 100
        imageSequence = 200
        tokenSequence = 1
        this.scenario = scenario
        now = time
        tokens.clear(); refresh.clear(); trackings.clear(); photos.clear(); receipts.clear(); users.clear()
        seed()
    }
    suspend fun advanceTo(time: Instant) = mutex.withLock { require(time >= now); now = time }
    private fun id() = "00000000-0000-0000-0000-${(sequence++).toString().padStart(12, '0')}"
    private fun seed() {
        for ((id, role) in listOf(USER to "user", ADMIN to "admin")) users[id] = obj(
            "id" to id, "firstName" to "Fixture", "lastName" to role.replaceFirstChar { it.uppercase() },
            "email" to "$role@example.invalid", "phoneNumber" to "+420000000000", "role" to role,
            "maximumDistance" to 100, "overallDistance" to 0)
        val imageId = "00000000-0000-0000-0000-000000000003"
        photos[imageId] = localPng()
        vehicle = obj("id" to VEHICLE.toLong(), "brand" to obj("id" to 2147483649L, "name" to "Fixture", "imageURL" to "$ORIGIN/images/$imageId"),
            "fullName" to "Fixture vehicle", "color" to "black", "year" to "2026", "model" to "Fixture", "spz" to "TEST",
            "transferableSpz" to false, "maximumDistance" to 100, "totalDistance" to 0, "place" to "Test",
            "driverLicenses" to JsonArray(listOf(obj("type" to "B"))), "images" to JsonArray(listOf(image(imageId, 0))))
    }
    private fun image(id: String, position: Int) = obj("id" to id, "url" to "$ORIGIN/images/$id", "position" to position)
    private fun tokens(user: String): JsonObject {
        val access = "mock-access-$epoch-${tokenSequence++}"
        val renewal = "mock-refresh-$epoch-${tokenSequence++}"
        tokens[access] = user; refresh[renewal] = user
        return obj("accessToken" to access, "refreshToken" to renewal)
    }
    private fun log(id: String, state: String, actor: String, message: JsonElement = JsonNull) = obj(
        "trackingId" to id, "state" to state, "message" to message, "assignedBy" to users.getValue(actor),
        "assignedAt" to now.toString(), "images" to JsonArray(emptyList()))
    private fun latest(t: JsonObject) = t["stateLogs"]!!.jsonArray.last().jsonObject.str("state")
    private fun terminal(t: JsonObject) = latest(t) in setOf("completed", "rejected", "failed")
    private fun owner(t: JsonObject) = t["user"]!!.jsonObject.str("id")
    private fun hydrated(t: JsonObject) = t + mapOf(
        "vehicle" to vehicle, "user" to users.getValue(owner(t)),
        "stateLogs" to JsonArray(t["stateLogs"]!!.jsonArray.map { value ->
            val log = value.jsonObject
            JsonObject(log + ("assignedBy" to users.getValue(log["assignedBy"]!!.jsonObject.str("id"))))
        }))
    private fun failure(status: Int, message: String): Nothing = throw Rejected(status, message)
    private fun admin(user: String) { if (user != ADMIN) failure(403, "Admin required") }
    private fun access(t: JsonObject, user: String) { if (user != ADMIN && owner(t) != user) failure(403, "Owner required") }
    private fun tracking(id: String, user: String) = (trackings[id] ?: failure(404, "Reservation not found")).also { access(it, user) }
    private fun body(request: HttpRequestData): JsonObject = when (val b = request.body) {
        is TextContent -> Json.parseToJsonElement(b.text).jsonObject
        is ByteArrayContent -> Json.parseToJsonElement(b.bytes().decodeToString()).jsonObject
        else -> failure(400, "Expected JSON")
    }
    private fun list(items: List<JsonObject>, request: HttpRequestData, first: Int): JsonArray {
        val p = request.url.parameters
        val page = (p["page"]?.toIntOrNull() ?: first) - first
        val size = p["size"]?.toIntOrNull() ?: 20
        if (page < 0 || size !in 1..100) failure(400, "Invalid pagination")
        return JsonArray(if (scenario == Scenario.EMPTY) emptyList() else items.drop(page * size).take(size))
    }

    fun engine() = MockEngine { request -> mutex.withLock {
        val path = request.url.encodedPath
        fun reply(value: JsonElement, status: Int = 200, extra: Map<String, String> = emptyMap()) = respond(
            value.toString(), HttpStatusCode.fromValue(status), headersOf(*(mapOf(HttpHeaders.ContentType to "application/json") + extra).map { it.key to listOf(it.value) }.toTypedArray()))
        try {
            if (!api.contains(request.url)) failure(400, "MOCK blocks every foreign origin")
            if (scenario == Scenario.TIMEOUT) throw io.ktor.client.network.sockets.SocketTimeoutException("Deterministic MOCK timeout")
            if (scenario == Scenario.SERVER_ERROR) failure(500, "Deterministic mock server error")
            val method = request.method.value
            val segments = path.trim('/').split('/')
            if (path == "/auth/login" && method == "POST") {
                val b = body(request)
                val user = users.values.find { it.str("email") == b.str("email") }
                if (scenario == Scenario.BAD_AUTH || b.str("password") != PASSWORD || user == null) failure(401, "Invalid mock credentials")
                return@withLock reply(tokens(user.str("id")))
            }
            if (path == "/auth/refresh" && method == "POST") {
                val user = refresh.remove(body(request).str("refreshToken")) ?: failure(401, "Invalid refresh token")
                return@withLock reply(tokens(user))
            }
            val user = tokens[request.headers[HttpHeaders.Authorization]?.removePrefix("Bearer ")] ?: failure(401, "Sign in required")
            if (path == "/auth" && method == "POST") return@withLock reply(users.getValue(user))
            if (path == "/auth/logout" && method == "POST") {
                tokens.entries.removeAll { it.value == user }; refresh.entries.removeAll { it.value == user }
                return@withLock reply(JsonPrimitive(true))
            }
            if (path == "/brands" && method == "GET") return@withLock reply(JsonArray(listOf(vehicle["brand"]!!)))
            if (segments.first() == "images" && segments.size == 2 && method == "GET") {
                val image = segments[1]
                trackings.values.filter { t -> t["stateLogs"]!!.jsonArray.any { l -> l.jsonObject["images"]!!.jsonArray.any { it.jsonObject.str("id") == image } } }.forEach { access(it, user) }
                return@withLock respond(photos[image] ?: failure(404, "Image not found"), HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "image/png"))
            }
            if (path == "/vehicles" && method == "GET") {
                val q = request.url.parameters
                val matches = q.entries().all { (key, values) ->
                    val v = values.first()
                    when (key) {
                        "search" -> vehicle.str("fullName").contains(v, true) || vehicle.str("spz").contains(v, true)
                        "brandId" -> vehicle["brand"]!!.jsonObject.str("id") == v
                        "driverLicenses" -> v.split(',').contains("B")
                        "page", "size" -> true
                        else -> vehicle[key]?.jsonPrimitive?.content == v
                    }
                }
                return@withLock reply(list(if (matches) listOf(vehicle) else emptyList(), request, 1))
            }
            if (segments.first() == "vehicles" && segments.getOrNull(1) == VEHICLE) {
                if (segments.size == 2 && method == "GET") return@withLock reply(vehicle)
                if (segments.getOrNull(2) == "calendar" && method == "GET") return@withLock reply(calendar(request))
            }
            if (path == "/trackings" && method == "POST") {
                val b = body(request)
                if (b.str("vehicleId") != VEHICLE) failure(404, "Vehicle not found")
                val start = Instant.parse(b.str("startTime")); val end = Instant.parse(b.str("endTime"))
                if (start >= end) failure(400, "Start must precede end")
                if (trackings.values.any { (!terminal(it) && owner(it) == user) || (latest(it) !in setOf("rejected", "failed") && start < Instant.parse(it.str("endTime")) && end > Instant.parse(it.str("startTime"))) }) failure(400, "Active or overlapping reservation")
                val id = id()
                val t = obj("id" to id, "vehicle" to vehicle, "user" to users.getValue(user), "startTime" to start.toString(), "endTime" to end.toString(),
                    "startTachometer" to null, "endTachometer" to null, "finalDistance" to null, "stateLogs" to JsonArray(listOf(log(id, "pending", user))))
                trackings[id] = t
                return@withLock reply(t)
            }
            if ((path == "/trackings" || segments.first() == "users" && segments.size == 3 && segments[2] == "tracking") && method == "GET") {
                val q = request.url.parameters
                val requestedUser = if (segments.first() == "users") segments[1] else q["userId"]
                if (segments.first() == "users") admin(user)
                if (user != ADMIN && requestedUser != null && requestedUser != user) failure(403, "Owner required")
                val states = q["states"]?.split(',')?.filter { it.isNotBlank() }.orEmpty()
                if (states.any { it !in statesAllowed }) failure(400, "Unknown state")
                val items = trackings.values.filter { (user == ADMIN || owner(it) == user) && (requestedUser == null || owner(it) == requestedUser) &&
                    (q["vehicleId"] == null || q["vehicleId"] == VEHICLE) && (states.isEmpty() || latest(it) in states) &&
                    (segments.first() != "users" || q["includeActive"] == "true" || terminal(it)) }.map { JsonObject(hydrated(it)) }
                return@withLock reply(list(items, request, 0))
            }
            if (path == "/trackings/user/current" && method == "GET") {
                val t = trackings.values.firstOrNull { owner(it) == user && !terminal(it) }
                return@withLock if (t == null) respond("", HttpStatusCode.NoContent) else reply(JsonObject(hydrated(t)))
            }
            if (segments.first() == "trackings" && segments.size == 3 && segments[2] == "count" && method == "GET") {
                admin(user)
                if (segments[1] !in statesAllowed) failure(400, "Unknown state")
                return@withLock reply(JsonPrimitive(trackings.values.count { latest(it) == segments[1] }))
            }
            if (segments.first() == "trackings" && segments.size >= 2) {
                val t = tracking(segments[1], user)
                if (segments.size == 2 && method == "GET") return@withLock reply(JsonObject(hydrated(t)))
                if (segments.size == 3 && segments[2] == "logs" && method == "POST") return@withLock reply(transition(t, body(request), user))
            }
            val vehicleUpload = segments.size == 3 && segments[0] == "vehicles" && segments[1] == VEHICLE && segments[2] == "images"
            val trackingUpload = segments.size == 5 && segments[0] == "trackings" && segments[2] == "logs" && segments[4] == "images"
            if ((vehicleUpload || trackingUpload) && method == "POST") {
                admin(user)
                val t = if (trackingUpload) tracking(segments[1], user) else null
                val state = if (trackingUpload) segments[3] else ""
                val logs = t?.get("stateLogs")?.jsonArray
                if (t != null && (state !in setOf("approved", "active", "returned") || logs!!.none { it.jsonObject.str("state") == state })) failure(400, "Photo state unavailable")
                val position = request.url.parameters["position"]?.toIntOrNull() ?: failure(400, "Position required")
                if (position < 0) failure(400, "Invalid position")
                val bytes = multipartFile(request)
                if (bytes.isEmpty()) failure(400, "Empty image")
                val key = request.headers["Idempotency-Key"]
                val fingerprint = "$path:$position:${bytes.contentHashCode()}:${bytes.size}"
                val receipt = key?.let { receipts[it] }
                if (receipt != null && (receipt.fingerprint != fingerprint || !photos.getValue(receipt.image).contentEquals(bytes))) failure(409, "Upload key already used for a different request")
                val imageId = receipt?.image ?: "00000000-0000-0000-0000-${(imageSequence++).toString().padStart(12, '0')}"
                if (receipt == null) {
                    photos[imageId] = bytes
                    if (key != null) receipts[key] = Receipt(fingerprint, imageId)
                    val img = image(imageId, position)
                    if (t == null) vehicle = JsonObject(vehicle + ("images" to orderedImages(vehicle["images"]!!.jsonArray + img)))
                    else trackings[t.str("id")] = JsonObject(t + ("stateLogs" to JsonArray(logs!!.map { l ->
                        if (l.jsonObject.str("state") != state) l else JsonObject(l.jsonObject + ("images" to orderedImages(l.jsonObject["images"]!!.jsonArray + img)))
                    })))
                    if (scenario == Scenario.LOST_UPLOAD_RESPONSE) { scenario = Scenario.NORMAL; throw MockTimeout() }
                }
                return@withLock reply(if (t == null) vehicle else JsonObject(hydrated(trackings.getValue(t.str("id")))), extra = mapOf("X-Upload-Image-Id" to imageId))
            }
            failure(501, "Unsupported MOCK endpoint: $method $path (URL imports disabled)")
        } catch (e: Rejected) {
            reply(obj("error" to HttpStatusCode.fromValue(e.status).description, "message" to e.message, "path" to path, "status" to e.status, "timestamp" to now.toString()), e.status)
        } catch (e: IllegalArgumentException) {
            reply(obj("error" to "Bad Request", "message" to "Invalid mock request", "path" to path, "status" to 400, "timestamp" to now.toString()), 400)
        }
    } }

    private fun transition(t: JsonObject, b: JsonObject, user: String): JsonObject {
        val next = b.str("state")
        if (next == "completed") { if (owner(t) != user) failure(403, "Owner confirmation required") } else admin(user)
        val allowed = when (latest(t)) {
            "pending" -> setOf("approved", "rejected", "failed")
            "approved" -> setOf("active", "failed")
            "active" -> setOf("returned", "failed")
            "returned" -> setOf("completed", "failed")
            else -> emptySet()
        }
        if (next !in allowed) failure(409, "Invalid reservation transition")
        val mileage = b["tachometer"]?.jsonPrimitive?.intOrNull
        if (next in setOf("active", "returned")) {
            if (mileage == null || mileage < 0 || next == "returned" && mileage < t["startTachometer"]!!.jsonPrimitive.int) failure(400, "Invalid mileage")
        } else if (mileage != null) failure(400, "Mileage not allowed")
        var updated = t
        if (next == "active") updated = JsonObject(updated + ("startTachometer" to JsonPrimitive(mileage)))
        if (next == "returned") updated = JsonObject(updated + mapOf("endTachometer" to JsonPrimitive(mileage), "finalDistance" to JsonPrimitive(mileage!! - t["startTachometer"]!!.jsonPrimitive.int), "endTime" to JsonPrimitive(now.toString())))
        if (next == "completed") {
            val u = users.getValue(owner(t))
            users[owner(t)] = JsonObject(u + ("overallDistance" to JsonPrimitive(u["overallDistance"]!!.jsonPrimitive.int + t["finalDistance"]!!.jsonPrimitive.int)))
        }
        updated = JsonObject(updated + ("stateLogs" to JsonArray(t["stateLogs"]!!.jsonArray + log(t.str("id"), next, user, b["message"] ?: JsonNull))))
        trackings[t.str("id")] = updated
        return JsonObject(hydrated(updated))
    }
    private fun calendar(request: HttpRequestData): JsonObject {
        val zone = TimeZone.of(request.url.parameters["timezone"] ?: "UTC")
        val result = linkedMapOf<String, JsonElement>()
        val today = now.toLocalDateTime(zone).date
        val active = trackings.values.filter { latest(it) !in setOf("rejected", "failed") }
        val dates = mutableSetOf(today)
        active.forEach { t ->
            var d = maxOf(today, Instant.parse(t.str("startTime")).toLocalDateTime(zone).date)
            val end = Instant.parse(t.str("endTime")).toLocalDateTime(zone).date
            while (d <= end) { dates.add(d); d = d.plus(1, DateTimeUnit.DAY) }
        }
        for (date in dates.sorted()) {
            val slots = (16..40).map { LocalTime(it / 2, it % 2 * 30) }.filter { time ->
                val at = LocalDateTime(date, time).toInstant(zone)
                at > now && active.none { at + kotlin.time.Duration.parse("30m") > Instant.parse(it.str("startTime")) && at < Instant.parse(it.str("endTime")) }
            }
            result[date.toString()] = JsonArray(slots.map { JsonPrimitive(it.toString()) })
        }
        return JsonObject(result)
    }
    private suspend fun multipartFile(request: HttpRequestData): ByteArray {
        val raw = when (val content = request.body) {
            is OutgoingContent.ReadChannelContent -> content.readFrom().readRemaining().readByteArray()
            is OutgoingContent.ByteArrayContent -> content.bytes()
            is OutgoingContent.WriteChannelContent -> kotlinx.coroutines.coroutineScope {
                val channel = ByteChannel(autoFlush = true)
                val writer = launch { try { content.writeTo(channel) } finally { channel.close() } }
                channel.readRemaining().readByteArray().also { writer.join() }
            }
            else -> failure(400, "Multipart required")
        }
        val boundary = request.body.contentType?.parameter("boundary") ?: failure(400, "Missing boundary")
        val marker = "\r\n\r\n".encodeToByteArray()
        val start = raw.indexOfBytes(marker, 0)
        val end = raw.indexOfBytes("\r\n--$boundary".encodeToByteArray(), start + marker.size)
        if (start < 0 || end < 0 || !Regex("name=\"?file\"?(;|\\r|\\n)").containsMatchIn(raw.copyOfRange(0, start).decodeToString())) failure(400, "Binary file part required")
        return raw.copyOfRange(start + marker.size, end)
    }
}
private fun orderedImages(images: List<JsonElement>) = JsonArray(images.sortedBy { it.jsonObject["position"]!!.jsonPrimitive.int })
private val statesAllowed = setOf("pending", "approved", "rejected", "active", "returned", "completed", "failed")
private class Rejected(val status: Int, override val message: String) : Exception(message)
/** Transport failure after no response (also used after a committed upload). */
class MockTimeout : kotlinx.io.IOException("Deterministic MOCK timeout")
private fun JsonObject.str(key: String) = get(key)?.jsonPrimitive?.content ?: ""
private fun obj(vararg entries: Pair<String, Any?>) = JsonObject(entries.associate { (key, value) -> key to when (value) {
    null -> JsonNull
    is JsonElement -> value
    is Number -> JsonPrimitive(value)
    is Boolean -> JsonPrimitive(value)
    else -> JsonPrimitive(value.toString())
} })
private fun ByteArray.indexOfBytes(needle: ByteArray, start: Int): Int = (start.coerceAtLeast(0)..size - needle.size).firstOrNull { i -> needle.indices.all { this[i + it] == needle[it] } } ?: -1
// Existing project launcher artwork, embedded so API and Coil share a network-free image source.
@OptIn(ExperimentalEncodingApi::class)
private fun localPng() = Base64.decode("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")
