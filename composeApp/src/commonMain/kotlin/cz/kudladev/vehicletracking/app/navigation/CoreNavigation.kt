package cz.kudladev.vehicletracking.app.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import cz.kudladev.vehicletracking.app.AppState
import cz.kudladev.vehicletracking.core.presentation.vehicle_list.VehicleListScreenRoot
import kotlinx.serialization.Serializable

@Serializable
data object CoreRoot
@Serializable
data object VehicleList
@Serializable
data object Tracking
@Serializable
data object History
@Serializable
data object Settings

fun NavController.navigateToVehicleList(navOptions: NavOptions) = navigate(route = VehicleList, navOptions)
fun NavController.navigateToTracking(navOptions: NavOptions) = navigate(route = Tracking, navOptions)
fun NavController.navigateToHistory(navOptions: NavOptions) = navigate(route = History, navOptions)
fun NavController.navigateToSettings(navOptions: NavOptions) = navigate(route = Settings, navOptions)

fun NavGraphBuilder.coreNavigation(
    appState: AppState
) {
    navigation<CoreRoot>(
        startDestination = VehicleList
    ) {
        composable<VehicleList> {
            VehicleListScreenRoot(
                paddingValues = appState.paddingValues,
            )
        }
        composable<Tracking> {

        }
        composable<History> {

        }
        composable<Settings> {

        }
    }
}