package cz.kudladev.vehicletracking.network

import cz.kudladev.vehicletracking.model.Result
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class ResponseSafetyTest {
    @Test fun emptyResponseIsAllowedOnlyForNullableOrUnitCallers() = runTest {
        val client = client("", HttpStatusCode.NoContent)
        try {
            assertNull(assertIs<Result.Success<String?>>(safeCall<String?> { client.get("/current") }).data)
            assertEquals(Unit, assertIs<Result.Success<Unit>>(safeCall<Unit> { client.get("/delete") }).data)
            assertEquals(502, assertIs<Result.Error>(safeCall<String> { client.get("/required") }).error.status)
        } finally { client.close() }
    }

    @Test fun malformedSuccessIsAProtocolError() = runTest {
        val client = client("not JSON", HttpStatusCode.OK)
        try { assertEquals(502, assertIs<Result.Error>(safeCall<Int> { client.get("/number") }).error.status) }
        finally { client.close() }
    }

    @Test fun errorsKeepHttpStatusWithoutRequiringTheOldSpringShape() = runTest {
        for (code in listOf(400, 401, 403, 404, 409, 500)) {
            for (body in listOf("<html>gateway</html>", "", "{}", """{"message":"Denied","status":200}""", """{"message":{"field":"invalid"}}""")) {
                val client = client(body, HttpStatusCode.fromValue(code))
                try {
                    val result = assertIs<Result.Error>(safeCall<String> { client.get("/protected?token=test-only") })
                    assertEquals(code, result.error.status)
                    assertEquals("/protected", result.error.path)
                    assertFalse(result.error.message.contains("<html>"))
                } finally { client.close() }
            }
        }
    }

    @Test fun oversizedErrorDoesNotExposeTheBody() = runTest {
        val client = client("x".repeat(100_000), HttpStatusCode.BadGateway)
        try {
            val error = assertIs<Result.Error>(safeCall<String> { client.get("/gateway") }).error
            assertEquals(502, error.status)
            assertTrue(error.message.length < 512)
        } finally { client.close() }
    }

    @Test fun cancellationPropagatesAndMappingErrorsAreContained() = runTest {
        assertFailsWith<CancellationException> { safeCall<String> { throw CancellationException("test") } }
        assertFailsWith<CancellationException> { Result.Success("x").mapSuccess { throw CancellationException("test") } }
        assertEquals(502, assertIs<Result.Error>(Result.Success("invalid").mapSuccess { it.toInt() }).error.status)
    }

    private fun client(body: String, status: HttpStatusCode) = HttpClient(MockEngine {
        respond(body, status, headersOf("Content-Type", "application/json"))
    }) { install(ContentNegotiation) { json() } }
}
