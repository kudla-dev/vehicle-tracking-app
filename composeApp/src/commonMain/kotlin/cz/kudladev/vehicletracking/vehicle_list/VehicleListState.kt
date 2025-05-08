package cz.kudladev.vehicletracking.vehicle_list

import cz.kudladev.vehicletracking.core.domain.models.Brand

data class VehicleListState(
    val selectedView: VehicleListView = VehicleListView.Grid,
    val selectedBrand: Brand? = null,
    val place: String? = null,
)


enum class VehicleListView {
    Grid,
    List,
}