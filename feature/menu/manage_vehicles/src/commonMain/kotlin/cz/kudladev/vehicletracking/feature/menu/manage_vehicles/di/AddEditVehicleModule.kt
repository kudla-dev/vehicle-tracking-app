package cz.kudladev.vehicletracking.feature.menu.manage_vehicles.di

import cz.kudladev.vehicletracking.feature.menu.manage_vehicles.AddEditVehicleViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val addEditVehicleModule = module {
    viewModelOf(::AddEditVehicleViewModel)
}