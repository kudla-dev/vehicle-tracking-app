package cz.kudladev.vehicletracking.network.di

import cz.kudladev.vehicletracking.network.HttpClientFactory
import cz.kudladev.vehicletracking.network.AppConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf

import org.koin.dsl.module

expect val platformNetworkModule: Module

fun networkModule(config: AppConfig): Module {
    // Validate before Koin starts or any native network engine is created.
    val api = config.requireNetworkApi()
    return module {
        includes(platformNetworkModule)

        single { config }
        single { api }

        singleOf(::HttpClientFactory)

        single<HttpClient> {
            get<HttpClientFactory>()
                .create(get<HttpClientEngine>())
        }
    }
}

