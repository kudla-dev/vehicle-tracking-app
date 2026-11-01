package cz.kudladev.vehicletracking.network

enum class AppEnvironment {
    MOCK, DEV, STAGE, PROD
}

/** Immutable launch configuration. Platform hosts own defaults and environment selection. */
class AppConfig private constructor(
    val environment: AppEnvironment,
    val api: ApiConfig?,
) {
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
