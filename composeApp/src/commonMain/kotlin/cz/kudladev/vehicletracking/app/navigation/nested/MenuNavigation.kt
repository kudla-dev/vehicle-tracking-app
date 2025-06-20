package cz.kudladev.vehicletracking.app.navigation.nested

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cz.kudladev.vehicletracking.app.AppState
import cz.kudladev.vehicletracking.feature.menu.admin_settings.AdminSettings
import cz.kudladev.vehicletracking.feature.menu.admin_settings.AdminSettingsRoot
import cz.kudladev.vehicletracking.feature.menu.main.MenuScreenRoot
import cz.kudladev.vehicletracking.feature.menu.manage_vehicles.AddEditVehicleRoot
import cz.kudladev.vehicletracking.feature.menu.manage_vehicles.ManageVehiclesAddEdit
import cz.kudladev.vehicletracking.feature.vehicles.ManageVehicles
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
                    navController.navigate(AdminSettings) {
                        popUpTo(MenuRoot) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onManageVehicles = {
                    navController.navigate(VehicleList)
                },
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
    }
}