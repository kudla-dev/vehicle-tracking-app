package cz.kudladev.vehicletracking.feature.menu.manage_trackings.di

import cz.kudladev.vehicletracking.feature.menu.manage_trackings.ManageTrackingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val manageTrackingsModule = module {

    viewModelOf(::ManageTrackingsViewModel)

}