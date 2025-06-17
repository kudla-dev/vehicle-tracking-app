package cz.kudladev.vehicletracking.di

import cz.kudladev.vehicletracking.core.data.di.dataModule
import cz.kudladev.vehicletracking.core.datastore.datastoreModule
import cz.kudladev.vehicletracking.core.domain.di.domainModule
import cz.kudladev.vehicletracking.feature.onboarding.di.onBoardingModule
import cz.kudladev.vehicletracking.feature.search.di.searchModule
import cz.kudladev.vehicletracking.feature.vehicledetail.di.vehicleDetailModule
import cz.kudladev.vehicletracking.feature.vehicles.di.vehiclesModule
import cz.kudladev.vehicletracking.network.di.networkModule
import org.koin.dsl.module


val featureModule = module {
    includes(onBoardingModule)
    includes(vehiclesModule)
    includes(vehicleDetailModule)
    includes(searchModule)
}
val appModules = listOf(
    dataModule,
    networkModule,
    featureModule
)


