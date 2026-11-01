package cz.kudladev.vehicletracking.network

import cz.kudladev.vehicletracking.core.datastore.StorageScope

enum class AppEnvironment {
    MOCK, DEV, STAGE, PROD
}

/** Immutable launch configuration. Platform hosts own defaults and environment selection. */
class AppConfig private constructor(
    val environment: AppEnvironment,
    val api: ApiConfig?,
) {
    // Normalize default ports, host case and trailing slash before deriving the namespace.
    val storageScope = StorageScope(environment.name.lowercase(), api?.let {
        "${it.url.protocol.name}://${it.url.host.lowercase()}:${it.url.port}"
    } ?: "offline")

    fun requireNetworkApi(): ApiConfig = checkNotNull(api) {
        "Mock requires a dedicated transport; a real network engine is not allowed"
    }

    companion object {
        fun create(environment: AppEnvironment, apiBaseUrl: String? = null): AppConfig {
            if (environment == AppEnvironment.MOCK) {
                require(apiBaseUrl == null) { "Mock must not have a remote API origin" }
                return AppConfig(environment, null)
            }
            require(!apiBaseUrl.isNullOrBlank()) { "An explicit API origin is required" }
            return AppConfig(environment, ApiConfig(
                apiBaseUrl,
                requireHttps = environment == AppEnvironment.STAGE || environment == AppEnvironment.PROD,
            ))
        }
    }
}
