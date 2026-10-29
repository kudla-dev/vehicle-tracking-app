package cz.kudladev.vehicletracking.core.data.contract

import cz.kudladev.vehicletracking.core.data.auth.models.*
import cz.kudladev.vehicletracking.core.data.tracking.TrackingRepositoryImpl
import cz.kudladev.vehicletracking.core.data.tracking.models.*
import cz.kudladev.vehicletracking.core.data.vehicles.VehicleRepositoryImpl
import cz.kudladev.vehicletracking.core.data.vehicles.models.*
import cz.kudladev.vehicletracking.core.data.images.upload.ImageUploadClient
import cz.kudladev.vehicletracking.core.data.images.upload.awaitUploadedImage
import cz.kudladev.vehicletracking.menu.manage_vehicles.data.VehicleRequest
import cz.kudladev.vehicletracking.model.*
import cz.kudladev.vehicletracking.network.responseToResult
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.*
import kotlin.test.*

class SharedFixtureTest {
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }
    private val fixtures = json.parseToJsonElement(requireNotNull(javaClass.getResource("/contract/api.json")).readText()).jsonObject
    private fun fixture(name: String) = fixtures.getValue(name).toString()
    private fun client(body: String, status: Int = 200) = HttpClient(MockEngine {
        respond(body, HttpStatusCode.fromValue(status), headersOf("Content-Type", "application/json"))
    }) { install(ContentNegotiation) { json(json) } }

    @Test fun authAndImageRequestShapesStayCompatible() {
        assertEquals(fixtures["login"], json.encodeToJsonElement(json.decodeFromString<LoginRequest>(fixture("login"))))
        assertEquals(fixtures["register"], json.encodeToJsonElement(json.decodeFromString<RegisterRequest>(fixture("register"))))
        assertEquals(fixtures["refresh"], json.encodeToJsonElement(json.decodeFromString<RefreshRequest>(fixture("refresh"))))
        assertEquals(fixtures["tokens"], json.encodeToJsonElement(json.decodeFromString<TokenPair>(fixture("tokens"))))
        assertEquals(fixtures["imagePosition"], json.encodeToJsonElement(json.decodeFromString<VehicleImageRequest>(fixture("imagePosition"))))
        assertEquals(1, json.decodeFromString<Map<String, List<String>>>(fixture("calendar")).toCalendarMap().size)
    }

    @Test fun uploadSuccessUsesParentFixtureAndVerifiesImageBytes() = runTest {
        val bytes = byteArrayOf(1, 2, 3)
        val c = HttpClient(MockEngine { request ->
            when {
                request.url.encodedPath.startsWith("/images/") -> respond(bytes)
                request.method == HttpMethod.Post -> respond(fixture("uploadedVehicle"), headers = headersOf("Content-Type", "application/json"))
                else -> respond(fixture("vehicle"), headers = headersOf("Content-Type", "application/json"))
            }
        }) { install(ContentNegotiation) { json(json) } }
        try {
            val result = assertIs<Result.Success<ImageWithUrl>>(ImageUploadClient(c).vehicle(bytes, 2147483648L, 0).awaitUploadedImage())
            assertEquals(json.decodeFromString<ImageWithUrl>(fixture("image")), result.data)
        } finally { c.close() }
    }

    @Test fun numericIdsSurviveResponseDomainRequestAndLegacyNumbers() = runTest {
        val dto = json.decodeFromString<VehicleBasic>(fixture("vehicle"))
        assertEquals(2147483648L, dto.toDomain().id)
        assertEquals(2147483649L, dto.toDomain().brand?.id)
        val request = json.decodeFromString<VehicleRequest>(fixture("vehicleRequest"))
        assertEquals(fixtures["vehicleRequest"], json.encodeToJsonElement(request))
        val create = json.decodeFromString<TrackingCreate>(fixture("trackingCreate"))
        assertEquals(2147483648L, create.vehicleId)
        assertEquals(fixtures["trackingCreate"], json.encodeToJsonElement(create))
        assertEquals(fixtures["trackingLogRequest"], json.encodeToJsonElement(json.decodeFromString<TrackingLogRequest>(fixture("trackingLogRequest"))))
        for (id in listOf(7L, Int.MAX_VALUE.toLong(), 2147483648L, Long.MAX_VALUE)) {
            val body = JsonObject(fixtures.getValue("vehicle").jsonObject + ("id" to JsonPrimitive(id))).toString()
            val c = client(body)
            try { assertEquals(id, assertIs<Result.Success<Vehicle>>(VehicleRepositoryImpl(c).getById(id)).data.id) }
            finally { c.close() }
        }
        // Reproduce why the old Int consumer cannot support the new fixture.
        assertFails { json.decodeFromString<Int>("2147483648") }
    }

    @Test fun largeIdsReachDetailAndCalendarPathsAndPersistedModels() = runTest {
        for (id in listOf(Int.MAX_VALUE.toLong(), 2147483648L, Long.MAX_VALUE)) {
            val domain = json.decodeFromString<VehicleBasic>(fixture("vehicle")).toDomain().copy(id = id)
            assertEquals(id, json.decodeFromString<Vehicle>(json.encodeToString(domain)).id)
            val paths = mutableListOf<String>()
            val c = HttpClient(MockEngine { request ->
                paths += request.url.encodedPath
                respond(if (request.url.encodedPath.endsWith("/calendar")) fixture("calendar") else
                    JsonObject(fixtures.getValue("vehicle").jsonObject + ("id" to JsonPrimitive(id))).toString(),
                    headers = headersOf("Content-Type", "application/json"))
            }) { install(ContentNegotiation) { json(json) } }
            try {
                val repository = VehicleRepositoryImpl(c)
                assertIs<Result.Success<Vehicle>>(repository.getById(id))
                assertIs<Result.Success<*>>(repository.getCalendar(id))
                assertEquals(listOf("/vehicles/$id", "/vehicles/$id/calendar"), paths)
            } finally { c.close() }
        }
    }

    @Test fun nullableFieldsEmptyLogsPhotosAndScrapeKeepTheirMeaning() {
        val tracking = json.decodeFromString<TrackingResponse>(fixture("tracking")).toDomain()
        assertTrue(tracking.stateLogs.isEmpty())
        assertNull(tracking.startTachometer)
        assertNull(tracking.endTachometer)
        assertNull(tracking.finalDistance)
        val log = json.decodeFromString<TrackingLogDTO>(fixture("nullableLog")).toDomain()
        assertNull(log.assignedAt); assertNull(log.assignedBy); assertNull(log.message)
        assertTrue(requireNotNull(log.images).isEmpty())
        assertEquals(0, json.decodeFromString<ImageWithUrl>(fixture("image")).position)
        assertEquals(setOf("B"), json.decodeFromString<VehicleScrape>(fixture("scrape")).toDomain().driverLicenses.map { it.type }.toSet())
    }

    @Test fun queryFixturesMatchRepositoryRequestsAndEmptyPages() = runTest {
        val queries = fixtures.getValue("queries").jsonObject
        val c = HttpClient(MockEngine { request ->
            val key = when (request.url.encodedPath) { "/vehicles" -> "vehicles"; "/trackings" -> "trackings"; else -> "history" }
            val expected = Url("https://example.invalid" + queries.getValue(key).jsonPrimitive.content)
            assertEquals(expected.encodedPath, request.url.encodedPath)
            assertEquals(expected.parameters, request.url.parameters)
            respond(fixture("emptyPage"), headers = headersOf("Content-Type", "application/json"))
        }) { install(ContentNegotiation) { json(json) } }
        try {
            assertEquals(emptyList(), assertIs<Result.Success<List<Vehicle>>>(VehicleRepositoryImpl(c).get(brandId=2147483649L, driverLicense="B", page=1, size=1)).data)
            val repo = TrackingRepositoryImpl(c)
            assertEquals(emptyList(), assertIs<Result.Success<List<Tracking>>>(repo.getTrackings(listOf(TrackingState.PENDING), null, 2147483648L, 0, 1)).data)
            assertEquals(emptyList(), assertIs<Result.Success<List<Tracking>>>(repo.getUserTrackingHistory("00000000-0000-0000-0000-000000000001", 0, 1, false)).data)
        } finally { c.close() }
    }

    @Test fun noContentIsValidOnlyForNullableOrUnitConsumers() = runTest {
        val f = fixtures.getValue("noContent").jsonObject
        val c = client(f.getValue("body").jsonPrimitive.content, f.getValue("status").jsonPrimitive.int)
        try {
            assertNull(assertIs<Result.Success<Tracking?>>(TrackingRepositoryImpl(c).getCurrentTracking()).data)
            assertIs<Result.Success<Unit>>(responseToResult<Unit>(c.get("/empty")))
            assertEquals(502, assertIs<Result.Error>(VehicleRepositoryImpl(c).getById(2147483648L)).error.status)
        } finally { c.close() }
    }

    @Test fun errorEnvelopesAndNonJsonGatewayRetainHttpStatus() = runTest {
        for (f in fixtures.getValue("errors").jsonArray) {
            val error = f.jsonObject
            val status = error.getValue("status").jsonPrimitive.int
            val c = client(f.toString(), status)
            try {
                val result = assertIs<Result.Error>(VehicleRepositoryImpl(c).getById(2147483648L))
                assertEquals(status, result.error.status)
                assertEquals(error.getValue("message").jsonPrimitive.content, result.error.message)
            } finally { c.close() }
        }
        val gateway = fixtures.getValue("gateway").jsonObject
        val c = client(gateway.getValue("body").jsonPrimitive.content, gateway.getValue("status").jsonPrimitive.int)
        try { assertEquals(502, assertIs<Result.Error>(VehicleRepositoryImpl(c).getById(1L)).error.status) }
        finally { c.close() }
    }

    @Test fun upload409IsTerminalAndDoesNotAcknowledgePhoto() = runTest {
        val conflict = fixtures.getValue("errors").jsonArray.single { it.jsonObject.getValue("status").jsonPrimitive.int == 409 }
        var posts = 0
        val c = HttpClient(MockEngine { request ->
            if (request.method == HttpMethod.Get) respond(fixture("vehicle"), headers = headersOf("Content-Type", "application/json"))
            else { posts++; respond(conflict.toString(), HttpStatusCode.Conflict, headersOf("Content-Type", "application/json")) }
        }) { install(ContentNegotiation) { json(json) } }
        try {
            val result = ImageUploadClient(c).vehicle(byteArrayOf(1), 2147483648L, 0, "00000000-0000-0000-0000-000000000004").awaitUploadedImage()
            assertEquals(409, assertIs<Result.Error>(result).error.status)
            assertEquals(1, posts)
        } finally { c.close() }
    }

    @Test fun countersAlsoPreserveServerLongWidth() = runTest {
        val c = client(fixture("count"))
        try { assertEquals(2147483648L, assertIs<Result.Success<Long>>(TrackingRepositoryImpl(c).getCountByState(TrackingState.PENDING)).data) }
        finally { c.close() }
    }
}
