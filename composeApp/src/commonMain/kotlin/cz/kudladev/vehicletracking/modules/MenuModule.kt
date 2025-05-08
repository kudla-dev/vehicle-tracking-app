package cz.kudladev.vehicletracking.modules

import cz.kudladev.vehicletracking.menu.admin_settings.AdminSettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val menuModule = module {

    viewModelOf(::AdminSettingsViewModel)

}