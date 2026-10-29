package cz.kudladev.vehicletracking.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class ApiConfigTest {
    @Test fun validatesAnOriginInsteadOfSilentlyDroppingItsPrefix() {
        for (url in listOf("", "host:8080", "ftp://host", "http://0.0.0.0:8080", "http://[::]:8080",
            "https://host/api", "https://host/?query=value", "https://host/#fragment", "https://user:pass@host")) {
            assertFailsWith<IllegalArgumentException>(url) { ApiConfig(url) }
        }
        assertEquals("example.invalid", ApiConfig("https://example.invalid").url.host)
        assertFalse(ApiConfig("https://example.invalid").contains(ApiConfig("http://example.invalid").url))
        assertFalse(ApiConfig("https://example.invalid").contains(ApiConfig("https://example.invalid:8443").url))
    }

    @Test fun rootedRoutesUseConfiguredSchemeHostAndPort() = runTest {
        val paths = mutableListOf<String>()
        val client = HttpClient(MockEngine { request ->
            paths += request.url.toString()
            respond("ok")
        }) { apiEndpoint(ApiConfig("https://api.example.invalid:8443")) }
        try {
            client.get("/vehicles")
            client.get("/trackings?size=3")
            assertEquals(listOf("https://api.example.invalid:8443/vehicles", "https://api.example.invalid:8443/trackings?size=3"), paths)
        } finally { client.close() }
    }

    @Test fun externalImagesAndTheirChallengesNeverReceiveApiTokens() = runTest {
        val requests = mutableListOf<Pair<String, String?>>()
        var refreshCalls = 0
        val client = HttpClient(MockEngine { request ->
            requests += request.url.host to request.headers[HttpHeaders.Authorization]
            if (request.url.host == "images.example.invalid") {
                respond("denied", HttpStatusCode.Unauthorized, headersOf(HttpHeaders.WWWAuthenticate, "Bearer"))
            } else respond("ok")
        }) {
            apiEndpoint(ApiConfig("https://api.example.invalid"))
            install(Auth) { bearer {
                loadTokens { BearerTokens("test-only-token", "test-only-refresh") }
                refreshTokens { refreshCalls++; BearerTokens("test-only-refreshed", null) }
                sendWithoutRequest { true }
            } }
        }
        try {
            client.get("/vehicles")
            client.get("https://images.example.invalid/photo.jpg")
            assertEquals("Bearer test-only-token", requests.first().second)
            assertTrue(requests.filter { it.first == "images.example.invalid" }.all { it.second == null })
            assertEquals(1, requests.count { it.first == "images.example.invalid" })
            assertEquals(0, refreshCalls)
        } finally { client.close() }
    }

    @Test fun redirectToAnotherOriginDoesNotLeakAuthorization() = runTest {
        val externalHeaders = mutableListOf<String?>()
        val client = HttpClient(MockEngine { request ->
            if (request.url.host == "api.example.invalid") respond("", HttpStatusCode.Found,
                headersOf(HttpHeaders.Location, "https://images.example.invalid/photo.jpg"))
            else { externalHeaders += request.headers[HttpHeaders.Authorization]; respond("ok") }
        }) {
            apiEndpoint(ApiConfig("https://api.example.invalid"))
            install(Auth) { bearer { loadTokens { BearerTokens("test-only-token", null) }; sendWithoutRequest { true } } }
        }
        try {
            client.get("/photo")
            assertEquals(listOf<String?>(null), externalHeaders)
        } finally { client.close() }
    }
}
