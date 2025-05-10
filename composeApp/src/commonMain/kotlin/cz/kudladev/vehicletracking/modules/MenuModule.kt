package cz.kudladev.vehicletracking.modules

import cz.kudladev.vehicletracking.menu.admin_settings.AdminSettingsViewModel
import cz.kudladev.vehicletracking.menu.manage_vehicles.presentation.add_edit.AddEditVehicleViewModel
import cz.kudladev.vehicletracking.menu.manage_vehicles.presentation.list.ManageVehiclesScreenViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val menuModule = module {

    viewModelOf(::AdminSettingsViewModel)

    viewModelOf(::ManageVehiclesScreenViewModel)
    viewModelOf(::AddEditVehicleViewModel)



}