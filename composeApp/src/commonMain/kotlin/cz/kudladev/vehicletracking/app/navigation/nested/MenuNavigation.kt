package cz.kudladev.vehicletracking.app.navigation.nested

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import cz.kudladev.vehicletracking.app.AppState
import cz.kudladev.vehicletracking.feature.menu.admin_settings.AdminSettings
import cz.kudladev.vehicletracking.feature.menu.admin_settings.AdminSettingsRoot
import cz.kudladev.vehicletracking.feature.menu.main.MenuScreenRoot
import cz.kudladev.vehicletracking.feature.menu.manage_trackings.ManageTrackings
import cz.kudladev.vehicletracking.feature.menu.manage_trackings.ManageTrackingsRoot
import cz.kudladev.vehicletracking.feature.menu.manage_trackings.ManageTrackingsTypes
import cz.kudladev.vehicletracking.feature.menu.manage_vehicles.AddEditVehicleRoot
import cz.kudladev.vehicletracking.feature.menu.manage_vehicles.ManageVehiclesAddEdit
import cz.kudladev.vehicletracking.feature.menu.protocols.Protocols
import cz.kudladev.vehicletracking.feature.menu.protocols.ProtocolsRoot
import cz.kudladev.vehicletracking.feature.menu.protocols.ProtocolsType
import cz.kudladev.vehicletracking.feature.menu.tracking_detail.TrackingDetail
import cz.kudladev.vehicletracking.feature.menu.tracking_detail.TrackingDetailRoot
import cz.kudladev.vehicletracking.feature.vehicles.VehicleList
import cz.kudladev.vehicletracking.feature.vehicles.VehicleListScreenRoot
import kotlinx.serialization.Serializable

@Serializable
data object MenuRoot

@Composable
fun MenuNavigation(
    appState: AppState
){
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = MenuRoot
    ){
         composable<MenuRoot>{
            MenuScreenRoot(
                paddingValues = appState.paddingValues,
                onAdminSettings = {
                    navController.navigate(AdminSettings)
                },
                onManageVehicles = {
                    navController.navigate(VehicleList)
                },
                onActiveTrackings = {
                    navController.navigate(ManageTrackings(ManageTrackingsTypes.ACTIVE))
                },
                onNonStartedTrackings = {
                    navController.navigate(ManageTrackings(ManageTrackingsTypes.NOT_STARTED))
                },
                onNewRequestsTrackings = {
                    navController.navigate(ManageTrackings(ManageTrackingsTypes.REQUESTED))
                },
                onTrackingHistory = {
                    navController.navigate(ManageTrackings(ManageTrackingsTypes.HISTORY))
                }
            )
        }
        composable<AdminSettings> {
            AdminSettingsRoot(
                paddingValues = appState.paddingValues,
                onBack = {
                    navController.navigateUp()
                }
            )
        }
        composable<VehicleList>{
            VehicleListScreenRoot(
                paddingValues = appState.paddingValues,
                onSearch = {

                },
                onVehicleClick = {

                },
                onCreate = {
                    navController.navigate(ManageVehiclesAddEdit) {
                        popUpTo(VehicleList) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        composable<ManageVehiclesAddEdit> {
            AddEditVehicleRoot(
                paddingValues = appState.paddingValues,
                onBack = {
                    navController.navigateUp()
                }
            )
        }
        composable<ManageTrackings> {
            val type = it.toRoute<ManageTrackings>().type
            ManageTrackingsRoot(
                paddingValues = appState.paddingValues,
                type = type,
                onBack = {
                    navController.navigateUp()
                },
                onTrackingClicked = { tracking ->
                    navController.navigate(TrackingDetail(trackingId = tracking.id))
                }
            )
        }
        composable<TrackingDetail> {
            TrackingDetailRoot(
                paddingValues = appState.paddingValues,
                onBack = {
                    navController.navigateUp()
                },
                onPickUpProtocol = { trackingId, trackingState ->
                    navController.navigate(Protocols(type = ProtocolsType.PICKUP, trackingId = trackingId, trackingState = trackingState))
                },
                onReturnProtocol = { trackingId, trackingState ->
                    navController.navigate(Protocols(type = ProtocolsType.RETURN, trackingId = trackingId, trackingState = trackingState))
                }
            )
        }
        composable<Protocols> {
            val type = it.toRoute<Protocols>().type
            ProtocolsRoot(
                type = type,
                paddingValues = appState.paddingValues,
                onBack = {
                    navController.navigateUp()
                }
            )
        }
    }
}