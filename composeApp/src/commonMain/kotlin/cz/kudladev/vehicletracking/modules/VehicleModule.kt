package cz.kudladev.vehicletracking.modules

import cz.kudladev.vehicletracking.core.data.vehicle.VehicleRepositoryImpl
import cz.kudladev.vehicletracking.core.domain.vehicle.VehicleRepository
import cz.kudladev.vehicletracking.vehicle_list.VehicleListViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind

val vehicleModule = module {

    viewModelOf(::VehicleListViewModel)
    singleOf(::VehicleRepositoryImpl).bind<VehicleRepository>()

}