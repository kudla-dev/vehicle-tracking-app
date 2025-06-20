package cz.kudladev.vehicletracking.feature.tracking.di

import cz.kudladev.vehicletracking.feature.tracking.TrackingScreenViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val trackingModule = module {
    viewModelOf(::TrackingScreenViewModel)
}