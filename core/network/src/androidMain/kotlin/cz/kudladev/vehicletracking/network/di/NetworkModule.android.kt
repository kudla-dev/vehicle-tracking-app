package cz.kudladev.vehicletracking.network.di

import cz.kudladev.vehicletracking.network.ApiConfig
import cz.kudladev.vehicletracking.core.network.BuildConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.android.Android
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformNetworkModule: Module
    get() = module {
        single { ApiConfig(BuildConfig.API_BASE_URL) }
        single<HttpClientEngine>{
            Android.create()
        }
    }