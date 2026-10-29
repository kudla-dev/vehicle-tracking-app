package cz.kudladev.vehicletracking.core.data.contract

import cz.kudladev.vehicletracking.core.data.tracking.TrackingRepositoryImpl
import cz.kudladev.vehicletracking.core.data.vehicles.VehicleRepositoryImpl
import cz.kudladev.vehicletracking.core.data.vehicles.models.VehicleScrape
import cz.kudladev.vehicletracking.core.data.vehicles.models.toDomain
import cz.kudladev.vehicletracking.model.Result
import cz.kudladev.vehicletracking.model.TrackingState
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.contentType
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.content.OutgoingContent
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.*
import kotlin.test.*

class RepositoryContractTest {
    private val vehicle = """{"id":7,"brand":{"id":1,"name":"Fixture","imageURL":""},"fullName":"Fixture vehicle","color":"black","year":"2026","model":"Fixture","spz":"TEST","transferableSpz":false,"maximumDistance":100,"totalDistance":0,"place":"Test","driverLicenses":[{"type":"B"}],"images":[]}"""

    @Test fun vehicleCreationEncodesLicenseObjectsInsteadOfAString() = runTest {
        var body: JsonObject? = null
        val client = client { request ->
            assertEquals(HttpMethod.Post, request.method)
            body = Json.parseToJsonElement((request.body as OutgoingContent.ByteArrayContent).bytes().decodeToString()).jsonObject
            vehicle
        }
        try {
            val result = VehicleRepositoryImpl(client).createVehicle("Fixture vehicle", 1, "Fixture", "2026", "black", "TEST", false, 0, 100, " B, A, B ", "Test")
            assertIs<Result.Success<*>>(result)
            assertEquals(Json.parseToJsonElement("""[{"type":"B"},{"type":"A"}]"""), body?.get("driverLicense"))
            assertFalse(requireNotNull(body).containsKey("driverLicenses"))
        } finally { client.close() }
    }

    @Test fun vehicleFilterUsesTheControllerParameterName() = runTest {
        val client = client { request ->
            assertEquals("B", request.url.parameters["driverLicenses"])
            assertNull(request.url.parameters["driverLicense"])
            assertNull(request.url.parameters["type"])
            assertNull(request.url.parameters["subType"])
            "[]"
        }
        try {
            assertIs<Result.Success<*>>(VehicleRepositoryImpl(client).get(driverLicense = "B", type = "unsupported", subType = "unsupported", page = 1, size = 3))
        } finally { client.close() }
    }

    @Test fun trackingAndHistoryQueriesUseSizeAndPreservePagingConventions() = runTest {
        val paths = mutableListOf<String>()
        val client = client { request ->
            paths += request.url.encodedPath
            assertEquals("3", request.url.parameters["size"])
            assertEquals("0", request.url.parameters["page"])
            assertNull(request.url.parameters["pageSize"])
            if (request.url.encodedPath == "/trackings") assertEquals("7", request.url.parameters["vehicleId"])
            "[]"
        }
        try {
            val repo = TrackingRepositoryImpl(client)
            assertIs<Result.Success<*>>(repo.getTrackings(emptyList(), null, 7, 0, 3))
            assertIs<Result.Success<*>>(repo.getUserTrackingHistory("user-1", 0, 3, false))
            assertEquals(listOf("/trackings", "/users/user-1/tracking"), paths)
        } finally { client.close() }
    }

    @Test fun scrapePreservesBackendLicenseMetadata() {
        val dto = Json { ignoreUnknownKeys = true }.decodeFromString<VehicleScrape>(
            """{"fullName":"Fixture","driverLicense":["A","B"],"imageUrls":["https://example.invalid/photo.jpg"]}"""
        )
        assertEquals(setOf("A", "B"), dto.toDomain().driverLicenses.map { it.type }.toSet())
    }

    @Test fun stateCounterUsesSupportedWireStateAndDecodesNumber() = runTest {
        val client = client { request -> assertEquals("/trackings/pending/count", request.url.encodedPath); "4" }
        try {
            val result = TrackingRepositoryImpl(client).getCountByState(TrackingState.PENDING)
            assertEquals(4L, assertIs<Result.Success<Long>>(result).data)
        } finally { client.close() }
    }

    private fun client(response: (io.ktor.client.request.HttpRequestData) -> String) = HttpClient(MockEngine { request ->
        respond(response(request), headers = headersOf("Content-Type", "application/json"))
    }) {
        defaultRequest { contentType(ContentType.Application.Json) }
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
    }
}
