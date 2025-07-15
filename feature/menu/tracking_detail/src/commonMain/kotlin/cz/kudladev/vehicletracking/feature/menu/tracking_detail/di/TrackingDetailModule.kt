package cz.kudladev.vehicletracking.feature.menu.tracking_detail.di

import cz.kudladev.vehicletracking.feature.menu.tracking_detail.TrackingDetailViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val trackingDetailModule = module {
    viewModelOf(::TrackingDetailViewModel)
}