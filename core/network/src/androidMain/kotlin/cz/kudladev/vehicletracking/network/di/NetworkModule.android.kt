package cz.kudladev.vehicletracking.network.di

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.android.Android
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformNetworkModule: Module
    get() = module {
        single<HttpClientEngine>{
            Android.create()
        }
    }