package cz.kudladev.vehicletracking.network

import cz.kudladev.vehicletracking.network.di.networkModule
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.HttpRedirect
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import org.koin.dsl.koinApplication
import kotlin.test.*

class AppConfigTest {
    @Test fun hostedEnvironmentsRequireExplicitHttpsOrigin() {
        for (environment in listOf(AppEnvironment.STAGE, AppEnvironment.PROD)) {
            for (url in listOf(null, "", " ", "http://localhost:8080", "http://api.example.invalid")) {
                assertFailsWith<IllegalArgumentException> { AppConfig.create(environment, url) }
            }
            val config = AppConfig.create(environment, "https://api.example.invalid:8443")
            assertTrue(config.requireNetworkApi().requireHttps)
            assertEquals(8443, config.requireNetworkApi().url.port)
        }
    }

    @Test fun developmentAcceptsExplicitLocalOrigin() {
        val config = AppConfig.create(AppEnvironment.DEV, "http://10.0.2.2:8080")
        assertFalse(config.requireNetworkApi().requireHttps)
        assertEquals("10.0.2.2", config.requireNetworkApi().url.host)
        assertFailsWith<IllegalArgumentException> { AppConfig.create(AppEnvironment.DEV) }
    }

    @Test fun mockCannotFallThroughToRealNetwork() {
        val config = AppConfig.create(AppEnvironment.MOCK)
        assertNull(config.api)
        assertFailsWith<IllegalStateException> { networkModule(config) }
        assertFailsWith<IllegalArgumentException> {
            AppConfig.create(AppEnvironment.MOCK, "https://api.example.invalid")
        }
    }

    @Test fun diUsesExactlyTheConfigurationSuppliedByTheHost() {
        val config = AppConfig.create(AppEnvironment.STAGE, "https://stage.example.invalid:8443")
        val application = koinApplication { modules(networkModule(config)) }
        try {
            assertSame(config, application.koin.get<AppConfig>())
            assertSame(config.api, application.koin.get<ApiConfig>())
        } finally { application.close() }
    }

    @Test fun invalidOriginsAreRejectedInEveryNetworkEnvironment() {
        for (environment in listOf(AppEnvironment.DEV, AppEnvironment.STAGE, AppEnvironment.PROD)) {
            for (url in listOf("https://host/api", "https://user:pass@host", "https://host/?q=1",
                "https://host/#fragment", "https://0.0.0.0", "https://host\n", "https://ho st")) {
                assertFailsWith<IllegalArgumentException> { AppConfig.create(environment, url) }
            }
        }
    }

    @Test fun hostedTransportBlocksHttpImagesBeforeSending() = runTest {
        var sent = 0
        val client = HttpClient(MockEngine { sent++; respond("ok") }) {
            apiEndpoint(AppConfig.create(AppEnvironment.PROD, "https://api.example.invalid").requireNetworkApi())
        }
        try {
            assertFailsWith<IllegalArgumentException> { client.get("http://images.example.invalid/photo") }
            assertEquals(0, sent)
            client.get("/vehicles")
            assertEquals(1, sent)
        } finally { client.close() }
    }

    @Test fun hostedTransportBlocksHttpsDowngradeRedirects() = runTest {
        val sent = mutableListOf<String>()
        val client = HttpClient(MockEngine { request ->
            sent += request.url.toString()
            respond("", HttpStatusCode.Found, headersOf(HttpHeaders.Location, "http://images.example.invalid/photo"))
        }) {
            install(HttpRedirect) { allowHttpsDowngrade = true }
            apiEndpoint(AppConfig.create(AppEnvironment.STAGE, "https://api.example.invalid").requireNetworkApi())
        }
        try {
            assertFailsWith<IllegalArgumentException> { client.get("/photo") }
            assertEquals(listOf("https://api.example.invalid/photo"), sent)
        } finally { client.close() }
    }
}
