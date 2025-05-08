package cz.kudladev.vehicletracking.app.nested

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cz.kudladev.vehicletracking.app.AppState
import cz.kudladev.vehicletracking.menu.admin_settings.AdminSettingsRoot
import cz.kudladev.vehicletracking.menu.root.MenuScreenRoot
import kotlinx.serialization.Serializable

@Serializable
data object MenuRoot
@Serializable
data object AdminSettings


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

    }
}