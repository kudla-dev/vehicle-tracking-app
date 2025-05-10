package cz.kudladev.vehicletracking.modules

import cz.kudladev.vehicletracking.core.data.brands.BrandRepositoryImpl
import cz.kudladev.vehicletracking.core.domain.BrandRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val brandModule = module {
    singleOf(::BrandRepositoryImpl).bind<BrandRepository>()
}