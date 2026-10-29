package cz.kudladev.vehicletracking.core.data.di

import org.koin.core.module.Module
import cz.kudladev.vehicletracking.core.data.images.ImageService
import cz.kudladev.vehicletracking.core.data.images.ImageRepositoryImpl
import cz.kudladev.vehicletracking.core.domain.images.ImageRepository
import org.koin.dsl.module
import org.koin.dsl.onClose
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind

actual val platformDataModule: Module
    get() = module {
        singleOf(::ImageService) onClose { it?.close() }
        singleOf(::ImageRepositoryImpl).bind<ImageRepository>()
    }
