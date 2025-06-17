package cz.kudladev.vehicletracking.app.navigation.nested

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cz.kudladev.vehicletracking.app.AppState
import cz.kudladev.vehicletracking.feature.menu.main.MenuScreenRoot
import kotlinx.serialization.Serializable

@Serializable
data object MenuRoot
@Serializable
data object AdminSettings
@Serializable
data object ManageVehicles
@Serializable
data object ManageVehiclesAddEdit

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
                    navController.navigate(ManageVehicles)
                },
            )
        }
//        composable<AdminSettings> {
//            AdminSettingsRoot(
//                paddingValues = appState.paddingValues,
//                onBack = {
//                    navController.navigateUp()
//                }
//            )
//        }
//        composable<ManageVehicles>{
//            ManageVehiclesScreenRoot(
//                paddingValues = appState.paddingValues,
//                onBack = {
//                    navController.navigateUp()
//                },
//                onEdit = {
//
//                },
//                onCreate = {
//                    navController.navigate(ManageVehiclesAddEdit) {
//                        popUpTo(ManageVehicles) {
//                            saveState = true
//                        }
//                        launchSingleTop = true
//                        restoreState = true
//                    }
//                }
//            )
//        }
//        composable<ManageVehiclesAddEdit> {
//            AddEditVehicleRoot(
//                paddingValues = appState.paddingValues,
//                onBack = {
//                    navController.navigateUp()
//                }
//            )
//        }

    }
}