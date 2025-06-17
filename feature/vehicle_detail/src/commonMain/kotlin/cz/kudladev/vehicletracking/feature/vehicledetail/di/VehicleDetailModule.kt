package cz.kudladev.vehicletracking.feature.vehicledetail.di

import cz.kudladev.vehicletracking.core.domain.di.domainModule
import cz.kudladev.vehicletracking.feature.vehicledetail.VehicleDetailViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val vehicleDetailModule = module {

    viewModelOf(::VehicleDetailViewModel)
}