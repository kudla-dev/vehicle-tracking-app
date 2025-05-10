package cz.kudladev.vehicletracking.menu.manage_vehicles.presentation.list

import cz.kudladev.vehicletracking.core.domain.models.Brand
import cz.kudladev.vehicletracking.vehicle_list.VehicleListView

data class ManageVehiclesScreenState(
    val selectedBrand: Brand? = null,
    val selectedView: VehicleListView = VehicleListView.Grid,
)