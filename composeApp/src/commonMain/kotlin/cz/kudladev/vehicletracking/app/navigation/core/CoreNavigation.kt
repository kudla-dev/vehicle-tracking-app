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
import cz.kudladev.vehicletracking.app.navigation.nested.MenuRoot
import cz.kudladev.vehicletracking.app.navigation.nested.VehicleNavigation
import cz.kudladev.vehicletracking.feature.favourite.FavouritesScreenRoot
import cz.kudladev.vehicletracking.feature.favourite.Favourites
import cz.kudladev.vehicletracking.feature.history.History
import cz.kudladev.vehicletracking.feature.history.HistoryScreenRoot
import cz.kudladev.vehicletracking.feature.tracking.Tracking
import cz.kudladev.vehicletracking.feature.tracking.TrackingScreenRoot
import cz.kudladev.vehicletracking.feature.vehicles.VehicleList
import kotlinx.serialization.Serializable

@Serializable
data object CoreRoot

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
            FavouritesScreenRoot(
                paddingValues = appState.paddingValues,
            )
        }
        composable<Tracking>(
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None },
        ) {
            MenuNavigation(
                appState = appState,
                startDestination = Tracking::class
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
            MenuNavigation(
                appState = appState,
                startDestination = MenuRoot::class
            )
        }
    }
}