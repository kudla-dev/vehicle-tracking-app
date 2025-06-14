package cz.kudladev.vehicletracking.app.navigation.core

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import cz.kudladev.vehicletracking.app.AppState
import cz.kudladev.vehicletracking.app.navigation.nested.MenuNavigation
import cz.kudladev.vehicletracking.app.navigation.nested.VehicleNavigation
import cz.kudladev.vehicletracking.history.HistoryScreenRoot
import cz.kudladev.vehicletracking.tracking.TrackingScreenRoot
import kotlinx.serialization.Serializable

@Serializable
data object CoreRoot
@Serializable
data object VehicleList
@Serializable
data object Favourites
@Serializable
data object Tracking
@Serializable
data object History
@Serializable
data object Menu

fun NavController.navigateToVehicleList(navOptions: NavOptions) = navigate(route = VehicleList, navOptions)
fun NavController.navigateToFavourites(navOptions: NavOptions) = navigate(route = Favourites, navOptions)
fun NavController.navigateToTracking(navOptions: NavOptions) = navigate(route = Tracking, navOptions)
fun NavController.navigateToHistory(navOptions: NavOptions) = navigate(route = History, navOptions)
fun NavController.navigateToSettings(navOptions: NavOptions) = navigate(route = Menu, navOptions)

fun NavGraphBuilder.coreNavigation(
    appState: AppState
) {
    navigation<CoreRoot>(
        startDestination = VehicleList
    ) {
        composable<VehicleList>(
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None },
        ) {
            VehicleNavigation(appState)
        }
        composable<Favourites>(
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None },
        ) {
//            VehicleListScreenRoot(
//                paddingValues = appState.paddingValues,
//            )
        }
        composable<Tracking>(
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None },
        ) {
            TrackingScreenRoot(
                paddingValues = appState.paddingValues,
            )
        }
        composable<History>(
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None },
        ) {
            HistoryScreenRoot(
                paddingValues = appState.paddingValues,
            )
        }
        composable<Menu>(
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None },
        ) {
            MenuNavigation(appState = appState)
        }
    }
}