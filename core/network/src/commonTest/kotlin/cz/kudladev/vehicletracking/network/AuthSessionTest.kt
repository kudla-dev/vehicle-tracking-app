package cz.kudladev.vehicletracking.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AuthSessionTest {

    private class FakeSessionManager(
        var accessToken: String? = null,
        var refreshToken: String? = null,
        var refreshResult: Pair<String, String>? = null
    ) : AuthSessionManager {
        var clearSessionCalled = false
        var refreshCalls = 0

        override suspend fun loadTokens(): Pair<String?, String?> {
            return accessToken to refreshToken
        }

        override suspend fun refreshTokens(
            refreshToken: String,
            refreshClient: HttpClient
        ): Pair<String, String>? {
            refreshCalls++
            if (refreshResult != null) {
                accessToken = refreshResult!!.first
                this.refreshToken = refreshResult!!.second
            } else {
                clearSession()
            }
            return refreshResult
        }

        override suspend fun clearSession() {
            clearSessionCalled = true
            accessToken = null
            refreshToken = null
        }
    }

    @Test
    fun unauthenticatedSession_doesNotSendAuthorizationHeader() = runTest {
        val session = FakeSessionManager(accessToken = null, refreshToken = null)
        val requests = mutableListOf<String?>()
        val engine = MockEngine { request ->
            requests.add(request.headers[HttpHeaders.Authorization])
            respond("ok")
        }
        val factory = HttpClientFactory(session, ApiConfig("https://api.example.com"))
        val client = factory.create(engine)

        try {
            client.get("/public/endpoint")
            assertEquals(1, requests.size)
            assertNull(requests[0], "No Authorization header should be sent when unauthenticated")
        } finally {
            client.close()
        }
    }

    @Test
    fun authenticatedSession_sendsBearerHeader() = runTest {
        val session = FakeSessionManager(accessToken = "valid-token", refreshToken = "valid-refresh")
        val requests = mutableListOf<String?>()
        val engine = MockEngine { request ->
            requests.add(request.headers[HttpHeaders.Authorization])
            respond("ok")
        }
        val factory = HttpClientFactory(session, ApiConfig("https://api.example.com"))
        val client = factory.create(engine)

        try {
            client.get("/protected/endpoint")
            assertEquals(1, requests.size)
            assertEquals("Bearer valid-token", requests[0])
        } finally {
            client.close()
        }
    }

    @Test
    fun expiredToken_triggersRefreshAndRetriesWithNewToken() = runTest {
        val session = FakeSessionManager(
            accessToken = "expired-token",
            refreshToken = "refresh-token",
            refreshResult = "new-access-token" to "new-refresh-token"
        )
        val requests = mutableListOf<String?>()
        var requestCount = 0

        val engine = MockEngine { request ->
            requests.add(request.headers[HttpHeaders.Authorization])
            requestCount++
            if (requestCount == 1) {
                // Return 401 with WWW-Authenticate header to trigger Ktor Auth refresh
                respond(
                    content = "unauthorized",
                    status = HttpStatusCode.Unauthorized,
                    headers = headersOf(HttpHeaders.WWWAuthenticate, "Bearer")
                )
            } else {
                respond("success")
            }
        }
        val factory = HttpClientFactory(session, ApiConfig("https://api.example.com"))
        val client = factory.create(engine)

        try {
            val response = client.get("/protected/endpoint")
            assertEquals(1, session.refreshCalls)
            assertEquals(2, requests.size)
            assertEquals("Bearer expired-token", requests[0])
            assertEquals("Bearer new-access-token", requests[1])
        } finally {
            client.close()
        }
    }

    @Test
    fun failedRefresh_clearsSessionAndDoesNotLoop() = runTest {
        val session = FakeSessionManager(
            accessToken = "expired-token",
            refreshToken = "invalid-refresh",
            refreshResult = null
        )
        var requestCount = 0

        val engine = MockEngine { _ ->
            requestCount++
            respond(
                content = "unauthorized",
                status = HttpStatusCode.Unauthorized,
                headers = headersOf(HttpHeaders.WWWAuthenticate, "Bearer")
            )
        }
        val factory = HttpClientFactory(session, ApiConfig("https://api.example.com"))
        val client = factory.create(engine)

        try {
            client.get("/protected/endpoint")
            assertEquals(1, session.refreshCalls)
            assertEquals(true, session.clearSessionCalled)
            assertNull(session.accessToken)
        } finally {
            client.close()
        }
    }
}
