package cz.kudladev.vehicletracking.app.navigation.core

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import cz.kudladev.vehicletracking.app.AppState
import cz.kudladev.vehicletracking.app.navigation.nested.Menu
import cz.kudladev.vehicletracking.app.navigation.nested.MenuNavigation
import cz.kudladev.vehicletracking.feature.favourite.Favourites
import cz.kudladev.vehicletracking.feature.history.History
import cz.kudladev.vehicletracking.feature.tracking.Tracking
import cz.kudladev.vehicletracking.feature.vehicles.VehicleList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data object CoreRoot
@Serializable
data object MenuRoot

@Serializable
data object VehicleRoot

@Serializable
data object FavouriteRoot

@Serializable
data object HistoryRoot

@Serializable
data object TrackingRoot

fun NavController.navigateToVehicleList(navOptions: NavOptions) = navigate(route = VehicleRoot, navOptions)
fun NavController.navigateToFavourites(navOptions: NavOptions) = navigate(route = FavouriteRoot, navOptions)
fun NavController.navigateToTracking(navOptions: NavOptions) = navigate(route = TrackingRoot, navOptions)
fun NavController.navigateToHistory(navOptions: NavOptions) = navigate(route = HistoryRoot, navOptions)
fun NavController.navigateToSettings(navOptions: NavOptions) = navigate(route = MenuRoot, navOptions)

fun NavGraphBuilder.coreNavigation(
    appState: AppState
) {
    navigation<CoreRoot>(
        startDestination = TrackingRoot
    ) {
        composable<VehicleRoot>(
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None },
        ) {
            MenuNavigation(
                appState = appState,
                startDestination = VehicleList::class
            )
        }
        composable<FavouriteRoot>(
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None },
        ) {
            MenuNavigation(
                appState = appState,
                startDestination = Favourites::class
            )
        }
        composable<TrackingRoot>(
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
        composable<HistoryRoot>(
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None },
        ) {
            MenuNavigation(
                appState = appState,
                startDestination = History::class
            )
        }
        composable<MenuRoot>(
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None },
        ) {
            MenuNavigation(
                appState = appState,
                startDestination = Menu::class
            )
        }
    }
}