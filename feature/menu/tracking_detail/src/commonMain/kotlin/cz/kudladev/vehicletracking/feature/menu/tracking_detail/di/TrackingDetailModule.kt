package cz.kudladev.vehicletracking.feature.menu.tracking_detail.di

import cz.kudladev.vehicletracking.feature.menu.tracking_detail.TrackingDetailViewModel
import cz.kudladev.vehicletracking.feature.menu.tracking_detail.tracking_detail_photo.TrackingDetailPhotoViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val trackingDetailModule = module {
    viewModelOf(::TrackingDetailViewModel)
    viewModelOf(::TrackingDetailPhotoViewModel)
}