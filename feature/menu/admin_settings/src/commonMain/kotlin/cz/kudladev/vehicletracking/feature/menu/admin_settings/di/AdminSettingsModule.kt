package cz.kudladev.vehicletracking.feature.menu.admin_settings.di

import cz.kudladev.vehicletracking.feature.menu.admin_settings.AdminSettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val adminSettingsModule = module {

    viewModelOf(::AdminSettingsViewModel)

}