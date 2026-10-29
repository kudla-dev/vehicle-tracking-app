package cz.kudladev.vehicletracking.network.di

import cz.kudladev.vehicletracking.network.ApiConfig
import platform.Foundation.NSBundle
import platform.Foundation.NSProcessInfo
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import org.koin.dsl.module

actual val platformNetworkModule: org.koin.core.module.Module
    get() = module {
        single {
            val environment = NSProcessInfo.processInfo.environment["MOTOPARK_API_BASE_URL"] as? String
            val bundled = NSBundle.mainBundle.objectForInfoDictionaryKey("MotoparkAPIBaseURL") as? String
            ApiConfig(environment ?: bundled ?: "http://127.0.0.1:8080")
        }
        single<HttpClientEngine> {
            Darwin.create()
        }
    }