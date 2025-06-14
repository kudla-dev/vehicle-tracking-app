package cz.kudladev.vehicletracking.modules

import cz.kudladev.vehicletracking.core.data.vehicle.VehicleRepositoryImpl
import cz.kudladev.vehicletracking.core.domain.VehicleRepository
import cz.kudladev.vehicletracking.menu.manage_vehicles.domain.BrandValidator
import cz.kudladev.vehicletracking.menu.manage_vehicles.domain.ColorValidator
import cz.kudladev.vehicletracking.menu.manage_vehicles.domain.FullNameValidator
import cz.kudladev.vehicletracking.menu.manage_vehicles.domain.MaximumDistanceValidator
import cz.kudladev.vehicletracking.menu.manage_vehicles.domain.ModelValidator
import cz.kudladev.vehicletracking.menu.manage_vehicles.domain.NameValidator
import cz.kudladev.vehicletracking.menu.manage_vehicles.domain.PlaceValidator
import cz.kudladev.vehicletracking.menu.manage_vehicles.domain.SpzValidator
import cz.kudladev.vehicletracking.menu.manage_vehicles.domain.YearValidator
import cz.kudladev.vehicletracking.vehicle_detail.VehicleDetailViewModel
import cz.kudladev.vehicletracking.vehicle_list.VehicleListViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind

val vehicleModule = module {

    viewModelOf(::VehicleListViewModel)
    viewModelOf(::VehicleDetailViewModel)
    singleOf(::VehicleRepositoryImpl).bind<VehicleRepository>()

    singleOf(::FullNameValidator)
    singleOf(::MaximumDistanceValidator)
    singleOf(::ModelValidator)
    singleOf(::SpzValidator)
    singleOf(::YearValidator)
    singleOf(::NameValidator)
    singleOf(::ColorValidator)
    singleOf(::PlaceValidator)
    singleOf(::BrandValidator)

}