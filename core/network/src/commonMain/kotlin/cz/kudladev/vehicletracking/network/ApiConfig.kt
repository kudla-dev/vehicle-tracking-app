package cz.kudladev.vehicletracking.network

import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.api.SendingRequest
import io.ktor.client.plugins.auth.AuthCircuitBreaker
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.http.Url
import io.ktor.http.contentType

/** API routes are rooted at '/', so accepting a path prefix would silently discard it. */
class ApiConfig(baseUrl: String, val requireHttps: Boolean = false) {
    val url: Url
    init {
        require(baseUrl.none { it.isWhitespace() || it.code < 32 || it == '\\' }) { "Invalid API URL characters" }
        require(baseUrl.startsWith("http://") || baseUrl.startsWith("https://")) { "API URL must be absolute HTTP(S)" }
        url = Url(baseUrl)
        require(!requireHttps || url.protocol == URLProtocol.HTTPS) { "This environment requires HTTPS" }
        require(url.host.isNotBlank() && url.host !in setOf("0.0.0.0", "::", "[::]")) { "API URL needs a reachable host" }
        require(url.encodedPath in listOf("", "/") && url.parameters.isEmpty() && url.fragment.isEmpty()) { "API URL must contain only an origin" }
        require(url.user.isNullOrEmpty() && url.password.isNullOrEmpty()) { "API URL must not contain credentials" }
        require(url.port in 1..65535) { "Invalid API port" }
    }
    fun contains(other: Url): Boolean = url.protocol == other.protocol && url.host == other.host && url.port == other.port
}

internal fun HttpClientConfig<*>.apiEndpoint(config: ApiConfig) {
    defaultRequest {
        url(config.url.toString())
        contentType(ContentType.Application.Json)
    }
    // This client also loads image URLs. Foreign servers must not receive session credentials,
    // including after a 401 challenge or redirect.
    install(createClientPlugin("ApiOriginGuard") {
        onRequest { request, _ ->
            require(!config.requireHttps || request.url.protocol == URLProtocol.HTTPS) { "This environment requires HTTPS" }
            if (!config.contains(request.url.build())) {
                request.attributes.put(AuthCircuitBreaker, Unit)
                request.headers.remove(HttpHeaders.Authorization)
            }
        }
        on(SendingRequest) { request, _ ->
            require(!config.requireHttps || request.url.protocol == URLProtocol.HTTPS) { "This environment requires HTTPS" }
            if (!config.contains(request.url.build())) request.headers.remove(HttpHeaders.Authorization)
        }
    })
}
