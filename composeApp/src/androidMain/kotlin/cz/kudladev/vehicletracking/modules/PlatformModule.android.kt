package cz.kudladev.vehicletracking.modules

import cz.kudladev.vehicletracking.core.data.image.ImageService
import cz.kudladev.vehicletracking.core.data.image.ImageWorker
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.android.Android
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformModule: Module
    get() = module {
        single<HttpClientEngine>{
            Android.create()
        }

        singleOf(::ImageService)
    }