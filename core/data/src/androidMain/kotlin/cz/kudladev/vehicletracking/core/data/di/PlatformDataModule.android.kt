package cz.kudladev.vehicletracking.core.data.di

import cz.kudladev.vehicletracking.core.data.images.ImageRepositoryImpl
import cz.kudladev.vehicletracking.core.data.images.ImageService
import cz.kudladev.vehicletracking.core.domain.images.ImageRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformDataModule: Module
    get() = module {
        singleOf(::ImageService)

        singleOf(::ImageRepositoryImpl).bind<ImageRepository>()

    }