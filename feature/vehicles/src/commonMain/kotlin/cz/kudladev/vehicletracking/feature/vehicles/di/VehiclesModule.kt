package cz.kudladev.vehicletracking.feature.vehicles.di

import cz.kudladev.vehicletracking.core.domain.di.domainModule
import cz.kudladev.vehicletracking.feature.vehicles.VehicleListViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val vehiclesModule = module {


    viewModelOf(::VehicleListViewModel)

}