package cz.kudladev.vehicletracking.network.di

import cz.kudladev.vehicletracking.network.HttpClientFactory
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf

import org.koin.dsl.module

expect val platformNetworkModule: Module

val networkModule = module {
    includes(platformNetworkModule)

    singleOf(::HttpClientFactory)

    single<HttpClient>{
        get<HttpClientFactory>()
            .create(get<HttpClientEngine>())
    }
}

