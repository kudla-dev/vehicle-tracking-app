package cz.kudladev.vehicletracking.mock

import cz.kudladev.vehicletracking.core.data.tracking.TrackingRepositoryImpl
import cz.kudladev.vehicletracking.core.data.vehicles.VehicleRepositoryImpl
import cz.kudladev.vehicletracking.core.data.tracking.models.TrackingResponse
import cz.kudladev.vehicletracking.model.Result
import cz.kudladev.vehicletracking.network.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.toList
import kotlinx.datetime.*
import kotlinx.serialization.json.*
import kotlin.test.*

class MockLifecycleTest {
    private class Session : AuthSessionManager {
        var access: String? = null
        override suspend fun loadTokens() = access to null
        override suspend fun clearSession() { access = null }
        override suspend fun refreshTokens(refreshToken: String, refreshClient: HttpClient): Pair<String, String>? = null
    }
    private class Harness {
        val server = MockServer()
        val session = Session()
        val client = HttpClientFactory(session, MockServer.api).create(server.engine())
        private val actors = mutableMapOf<String, HttpClient>()
        private fun actor(token: String) = actors.getOrPut(token) {
            HttpClientFactory(Session().also { it.access = token }, MockServer.api).create(server.engine())
        }
        fun close() { client.close(); actors.values.forEach { it.close() } }
        suspend fun login(role: String): String {
            // Explicit headers avoid the bearer plugin's cached credentials when switching identities.
            val response = client.post("/auth/login") { setBody("""{"email":"$role@example.invalid","password":"${MockServer.PASSWORD}"}""") }.body<JsonObject>()
            return response["accessToken"]!!.jsonPrimitive.content.also { if (session.access == null) session.access = it }
        }
        suspend fun get(path: String, token: String) = actor(token).get(path)
        suspend fun post(path: String, token: String, json: String) = actor(token).post(path) { setBody(json) }
        suspend fun create(user: String): JsonObject = post("/trackings", user, """{"vehicleId":${MockServer.VEHICLE},"startTime":"2026-11-03T10:00:00Z","endTime":"2026-11-03T12:00:00Z"}""").body()
        suspend fun transition(id: String, token: String, state: String, mileage: Int? = null) = post("/trackings/$id/logs", token, """{"state":"$state","message":null,"tachometer":$mileage}""")
        suspend fun upload(token: String, key: String, bytes: ByteArray, path: String = "/vehicles/${MockServer.VEHICLE}/images", position: Int = 0) = actor(token).submitFormWithBinaryData(path, formData {
            append("file", bytes, Headers.build { append(HttpHeaders.ContentType, "image/jpeg"); append(HttpHeaders.ContentDisposition, "filename=\"test.jpg\"") })
            append("position", position.toString())
        }) { bearerAuth(token); header("Idempotency-Key", key); parameter("position", position) }
    }
    @Test fun mockDiBindsOnlyOfflineClientAndRejectsHostedConfigurations() = runTest {
        assertFailsWith<IllegalArgumentException> {
            mockNetworkModule(AppConfig.create(AppEnvironment.PROD,"https://prod.example.invalid"))
        }
        val config = AppConfig.create(AppEnvironment.MOCK)
        val application = org.koin.dsl.koinApplication {
            modules(mockNetworkModule(config), org.koin.dsl.module { single<AuthSessionManager> { Session() } })
        }
        try {
            val koin = application.koin
            assertSame(config.storageScope,koin.get<cz.kudladev.vehicletracking.core.datastore.StorageScope>())
            assertNull(koin.getOrNull<io.ktor.client.engine.HttpClientEngine>())
            assertEquals(koin.get<MockServer>().now,cz.kudladev.vehicletracking.model.AppClock.now())
            assertEquals(401,koin.get<HttpClient>().get("/vehicles").status.value)
            assertEquals(400,koin.get<HttpClient>().get("https://example.invalid/photo.png").status.value)
        } finally {
            application.koin.get<HttpClient>().close()
            application.close()
            cz.kudladev.vehicletracking.model.AppClock.clock = Clock.System
        }
    }
    @Test fun lifecycleUsesActualRepositoryAndConsistentViews() = runTest {
        val h = Harness()
        try {
            val user = h.login("user")
            val repo = TrackingRepositoryImpl(h.client)
            val t = assertIs<Result.Success<*>>(repo.createTracking(MockServer.VEHICLE.toLong(), LocalDateTime(2026,11,3,10,0), LocalDateTime(2026,11,3,12,0))).data as cz.kudladev.vehicletracking.model.Tracking
            val id = t.id
            val admin = h.login("admin")
            assertEquals(1, h.get("/trackings/pending/count", admin).body<Int>())
            for ((state, mileage) in listOf("approved" to null, "active" to 100, "returned" to 125)) assertEquals(200, h.transition(id, admin, state, mileage).status.value)
            assertEquals(0, h.get("/trackings/pending/count", admin).body<Int>())
            assertEquals(200, h.transition(id, user, "completed").status.value)
            val detail = h.get("/trackings/$id", user).body<TrackingResponse>()
            assertEquals(listOf("pending", "approved", "active", "returned", "completed"), detail.stateLogs.map { it.state })
            assertEquals(25, detail.finalDistance)
            assertEquals(25, detail.user.overallDistance)
            assertEquals(204, h.get("/trackings/user/current", user).status.value)
            assertEquals(id, h.get("/users/${MockServer.USER}/tracking", admin).body<List<TrackingResponse>>().single().id)
            assertEquals(1, h.get("/trackings/completed/count", admin).body<Int>())
            assertEquals(409, h.transition(id, user, "completed").status.value)
        } finally { h.close() }
    }
    @Test fun actualRepositoryUploadConfirmsLocallyStoredBytes() = runTest {
        val h = Harness()
        try {
            h.login("admin")
            val bytes = byteArrayOf(0, -1, 13, 10, 42)
            val updates = VehicleRepositoryImpl(h.client).uploadImage(bytes, MockServer.VEHICLE.toLong(), 3).toList()
            assertFalse(updates.any { it is Result.Error })
            val final = assertIs<Result.Success<cz.kudladev.vehicletracking.core.domain.vehicles.ProgressUpdate>>(updates.last())
            assertNotNull(final.data.imageURL)
            assertEquals(3, final.data.imageURL!!.position)
        } finally { h.close() }
    }
    @Test fun concurrentCreationIsSerializedAndIdsIgnoreLoginCount() = runTest {
        val h = Harness()
        try {
            h.login("admin"); h.login("admin")
            val user = h.login("user")
            val replies = listOf(async { h.createResponse(user) }, async { h.createResponse(user) }).awaitAll()
            assertEquals(listOf(200, 400), replies.map { it.status.value }.sorted())
            assertEquals("00000000-0000-0000-0000-000000000100", replies.single { it.status.value == 200 }.body<JsonObject>().id())
        } finally { h.close() }
    }
    @Test fun sameApplicationClientCanSwitchFromOwnerToAdminAndBack() = runTest {
        val h=Harness()
        try {
            h.login("user")
            val id=h.create(requireNotNull(h.session.access)).id()
            assertEquals("user",h.client.post("/auth").body<JsonObject>()["role"]!!.jsonPrimitive.content)
            h.client.post("/auth/logout")
            h.session.clearSession()
            h.login("admin")
            assertEquals("admin",h.client.post("/auth").body<JsonObject>()["role"]!!.jsonPrimitive.content)
            assertEquals(200,h.client.post("/trackings/$id/logs") { setBody("{\"state\":\"approved\",\"tachometer\":null}") }.status.value)
            h.client.post("/auth/logout")
            h.session.clearSession()
            h.login("user")
            assertEquals("user",h.client.post("/auth").body<JsonObject>()["role"]!!.jsonPrimitive.content)
        } finally { h.close() }
    }
    @Test fun ownerReadScopeLogoutAndRefreshAreConsistent() = runTest {
        val h = Harness()
        try {
            val admin = h.login("admin"); val id = h.create(admin).id()
            val user = h.login("user")
            assertEquals(403, h.get("/trackings/$id",user).status.value)
            assertEquals(emptyList(),h.get("/trackings",user).body<List<JsonObject>>())
            assertEquals(403,h.get("/trackings?userId=${MockServer.ADMIN}",user).status.value)
            assertEquals(200,h.post("/auth/logout",user,"{}").status.value)
            assertEquals(401,h.get("/vehicles",user).status.value)
            assertEquals(401,h.post("/auth/refresh",admin,"{\"refreshToken\":\"invalid\"}").status.value)
        } finally { h.close() }
    }
    @Test fun timeoutReachesOrdinaryRepositoryAs408AndTimeIsControlled() = runTest {
        val h = Harness()
        try {
            h.server.reset(MockServer.Scenario.TIMEOUT)
            val result = assertIs<Result.Error>(VehicleRepositoryImpl(h.client).getById(MockServer.VEHICLE.toLong()))
            assertEquals(408,result.error.status)
            h.server.reset()
            h.server.advanceTo(Instant.parse("2026-11-03T09:15:00Z"))
            assertEquals(Instant.parse("2026-11-03T09:15:00Z"),h.server.now)
            assertFailsWith<IllegalArgumentException> { h.server.advanceTo(Instant.parse("2026-11-02T00:00:00Z")) }
        } finally { h.close() }
    }
    @Test fun rolesTransitionsAndMileageAreEnforced() = runTest {
        val h = Harness()
        try {
            val user = h.login("user"); val id = h.create(user).id()
            assertEquals(403, h.transition(id, user, "approved").status.value)
            assertEquals(403, h.get("/trackings/pending/count", user).status.value)
            assertEquals(403, h.upload(user,"no",byteArrayOf(1)).status.value)
            val admin = h.login("admin")
            assertEquals(409, h.transition(id, admin, "returned",100).status.value)
            assertEquals(200, h.transition(id, admin, "approved").status.value)
            assertEquals(400, h.transition(id, admin, "active",-1).status.value)
            assertEquals(200, h.transition(id, admin, "active",100).status.value)
            assertEquals(400, h.transition(id, admin, "returned",99).status.value)
            assertEquals(200, h.transition(id, admin, "returned",120).status.value)
            assertEquals(403, h.transition(id, admin, "completed").status.value)
        } finally { h.close() }
    }
    @Test fun rejectReleasesCalendarAndAllowsAnotherReservation() = runTest {
        val h = Harness()
        try {
            val user = h.login("user"); val t = h.create(user); val admin = h.login("admin")
            val occupied = h.get("/vehicles/${MockServer.VEHICLE}/calendar?timezone=UTC", user).body<JsonObject>()
            assertFalse(occupied["2026-11-03"]!!.jsonArray.contains(JsonPrimitive("10:00")))
            assertEquals(400, h.createResponse(user).status.value)
            assertEquals(200, h.transition(t.id(), admin,"rejected").status.value)
            val released = h.get("/vehicles/${MockServer.VEHICLE}/calendar?timezone=UTC", user).body<JsonObject>()
            assertNull(released["2026-11-03"]) // absent date means all standard working slots available
            assertEquals(200, h.createResponse(user).status.value)
        } finally { h.close() }
    }
    private suspend fun Harness.createResponse(user: String) = post("/trackings", user,"""{"vehicleId":${MockServer.VEHICLE},"startTime":"2026-11-03T10:00:00Z","endTime":"2026-11-03T12:00:00Z"}""")
    @Test fun uploadIdempotenceAndBytesSurviveLostResponse() = runTest {
        val h = Harness()
        try {
            h.server.reset(MockServer.Scenario.LOST_UPLOAD_RESPONSE)
            val admin = h.login("admin"); val bytes = ByteArray(100000) { (it % 251).toByte() }
            assertFailsWith<MockTimeout> { h.upload(admin,"stable-key", bytes) }
            val parent = h.get("/vehicles/${MockServer.VEHICLE}",admin).body<JsonObject>()
            val photo = parent["images"]!!.jsonArray.last().jsonObject
            assertContentEquals(bytes,h.get(photo["url"]!!.jsonPrimitive.content,admin).body<ByteArray>())
            val replay = h.upload(admin,"stable-key",bytes)
            assertEquals(photo.id(),replay.headers["X-Upload-Image-Id"])
            assertEquals(parent,replay.body<JsonObject>())
            assertEquals(409,h.upload(admin,"stable-key",byteArrayOf(9)).status.value)
            assertEquals(409,h.upload(admin,"stable-key",bytes,position=1).status.value)
        } finally { h.close() }
    }
    @Test fun protocolPhotosAppearOnEveryReservationView() = runTest {
        val h = Harness()
        try {
            val user=h.login("user"); val id=h.create(user).id();val admin=h.login("admin")
            h.transition(id,admin,"approved");h.transition(id,admin,"active",10)
            val path="/trackings/$id/logs/active/images"
            val photoId=h.upload(admin,"protocol",byteArrayOf(0,-1,2),path,2).headers["X-Upload-Image-Id"]
            val detail=h.get("/trackings/$id",user).body<JsonObject>()
            val current=h.get("/trackings/user/current",user).body<JsonObject>()
            val list=h.get("/trackings?states=active",admin).body<JsonArray>().single().jsonObject
            assertEquals(detail,current);assertEquals(detail,list)
            val photo=detail["stateLogs"]!!.jsonArray.last().jsonObject["images"]!!.jsonArray.single().jsonObject
            assertEquals(photoId,photo.id());assertEquals(2,photo["position"]!!.jsonPrimitive.int)
            assertContentEquals(byteArrayOf(0,-1,2),h.get(photo["url"]!!.jsonPrimitive.content,user).body<ByteArray>())
        } finally { h.close() }
    }
    @Test fun resetRestoresStableEntitiesClearsAuthPhotosAndReceipts() = runTest {
        val h=Harness()
        try {
            val user=h.login("user");val first=h.create(user).id();val admin=h.login("admin")
            val image=h.upload(admin,"key",byteArrayOf(1)).headers["X-Upload-Image-Id"]!!
            h.server.advanceTo(Instant.parse("2026-12-01T00:00:00Z"))
            h.server.reset()
            assertEquals(Instant.parse("2026-11-02T08:00:00Z"),h.server.now)
            assertEquals(401,h.get("/auth",user).status.value)
            val nextUser=h.login("user"); assertEquals(first,h.create(nextUser).id())
            val nextAdmin=h.login("admin")
            assertEquals(404,h.get("/images/$image",nextAdmin).status.value)
            assertEquals(200,h.upload(nextAdmin,"key",byteArrayOf(2)).status.value)
        } finally { h.close() }
    }
    @Test fun deterministicFailureScenariosAndUnknownEndpoints() = runTest {
        val h=Harness()
        try {
            h.server.reset(MockServer.Scenario.BAD_AUTH)
            assertEquals(401,h.client.post("/auth/login") { setBody("""{"email":"user@example.invalid","password":"${MockServer.PASSWORD}"}""") }.status.value)
            h.server.reset(MockServer.Scenario.SERVER_ERROR)
            assertEquals(500,h.client.get("/vehicles").status.value)
            h.server.reset(MockServer.Scenario.TIMEOUT)
            assertFailsWith<io.ktor.client.network.sockets.SocketTimeoutException> { h.client.get("/vehicles") }
            h.server.reset(MockServer.Scenario.EMPTY); val user=h.login("user")
            assertEquals(emptyList(),h.get("/vehicles",user).body<List<JsonObject>>())
            assertEquals(204,h.get("/trackings/user/current",user).status.value)
            h.server.reset();val admin=h.login("admin")
            assertEquals(501,h.post("/vehicles/${MockServer.VEHICLE}/images/fetch?url=https://example.com/photo",admin,"{\"position\":0}").status.value)
            assertEquals(501,h.get("/vehicles/scrape?url=http://127.0.0.1",admin).status.value)
            assertEquals(501,h.post("/unimplemented",admin,"{}").status.value)
        } finally { h.close() }
    }
    @Test fun everyForeignApiImageAndImportIsBlockedWithoutNativeEngine() = runTest {
        val h=Harness()
        try {
            val user=h.login("user")
            for(url in listOf("https://example.com/images/1","http://127.0.0.1:8080/vehicles","https://mock.motopark.invalid:444/vehicles")) {
                if (url.startsWith("http:")) assertFailsWith<IllegalArgumentException> { h.get(url,user) }
                else assertEquals(400,h.get(url,user).status.value)
            }
            val vehicle=assertIs<Result.Success<*>>(VehicleRepositoryImpl(h.client).getById(MockServer.VEHICLE.toLong())).data as cz.kudladev.vehicletracking.model.Vehicle
            assertEquals(MockServer.VEHICLE.toLong(),vehicle.id)
            val png=h.get("/images/00000000-0000-0000-0000-000000000003",user).body<ByteArray>()
            assertContentEquals(byteArrayOf(-119,80,78,71,13,10,26,10),png.take(8).toByteArray())
        } finally { h.close() }
    }
}
private fun JsonObject.id() = this["id"]!!.jsonPrimitive.content
