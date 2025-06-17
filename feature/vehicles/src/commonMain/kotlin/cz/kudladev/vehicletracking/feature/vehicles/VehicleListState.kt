package cz.kudladev.vehicletracking.feature.vehicles

import cz.kudladev.vehicletracking.model.Brand


data class VehicleListState(
    val selectedView: VehicleListView = VehicleListView.Grid,
    val selectedBrand: Brand? = null,
    val place: String? = null,
)


enum class VehicleListView {
    Grid,
    List,
}