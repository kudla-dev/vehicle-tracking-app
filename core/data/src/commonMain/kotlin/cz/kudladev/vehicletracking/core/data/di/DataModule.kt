package cz.kudladev.vehicletracking.core.data.di

import cz.kudladev.vehicletracking.core.data.auth.AuthRepositoryImpl
import cz.kudladev.vehicletracking.core.data.auth.UserStateHolderImpl
import cz.kudladev.vehicletracking.core.data.brands.BrandRepositoryImpl
import cz.kudladev.vehicletracking.core.data.tracking.TrackingRepositoryImpl
import cz.kudladev.vehicletracking.core.data.vehicles.VehicleRepositoryImpl
import cz.kudladev.vehicletracking.core.datastore.datastoreModule
import cz.kudladev.vehicletracking.core.domain.auth.AuthRepository
import cz.kudladev.vehicletracking.core.domain.auth.UserStateHolder
import cz.kudladev.vehicletracking.core.domain.brands.BrandRepository
import cz.kudladev.vehicletracking.core.domain.tracking.TrackingRepository
import cz.kudladev.vehicletracking.core.domain.vehicles.VehicleRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {

    includes(datastoreModule)
    includes(platformDataModule)

    singleOf(::AuthRepositoryImpl).bind<AuthRepository>()
    singleOf(::UserStateHolderImpl).bind<UserStateHolder>()
    singleOf(::BrandRepositoryImpl).bind<BrandRepository>()
    singleOf(::VehicleRepositoryImpl).bind<VehicleRepository>()
    singleOf(::TrackingRepositoryImpl).bind<TrackingRepository>()

}