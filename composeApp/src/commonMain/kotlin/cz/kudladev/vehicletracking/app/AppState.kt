package cz.kudladev.vehicletracking.app

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.util.trace
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import cz.kudladev.vehicletracking.app.navigation.core.CoreRoot
import cz.kudladev.vehicletracking.app.navigation.core.navigateToFavourites
import cz.kudladev.vehicletracking.app.navigation.core.navigateToHistory
import cz.kudladev.vehicletracking.app.navigation.core.navigateToSettings
import cz.kudladev.vehicletracking.app.navigation.core.navigateToTracking
import cz.kudladev.vehicletracking.app.navigation.core.navigateToVehicleList
import cz.kudladev.vehicletracking.auth.domain.UserStateHolder
import cz.kudladev.vehicletracking.core.presentation.components.basics.BottomBarDestinations
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.compose.koinInject


@Composable
fun rememberAppState(
    navController: NavHostController = rememberNavController(),
    userStateHolder: UserStateHolder = koinInject<UserStateHolder>()
): AppState {
    val coreNavController = navController

    return remember(
        coreNavController,
        userStateHolder
    ) {
        AppState(
            coreNavController = coreNavController,
            userStateHolder = userStateHolder
        )
    }
}

class AppState(
    val coreNavController: NavHostController,
    val userStateHolder: UserStateHolder
) {
    private val _isInCoreGraph = MutableStateFlow(false)
    val isInCoreGraph: StateFlow<Boolean> = _isInCoreGraph
    private val previousDestination = mutableStateOf<NavDestination?>(null)

    var paddingValues = PaddingValues()

    init {
        coreNavController.addOnDestinationChangedListener { _, _, _ ->
            updateCoreGraphState()
        }
        updateCoreGraphState()
    }


    val currentDestination: NavDestination?
        @Composable
        get() {
            val currentEntry = coreNavController.currentBackStackEntryFlow.collectAsState(initial = null)
            return currentEntry.value?.destination.also { destination ->
                if (destination != null) {
                    previousDestination.value = destination
                }
            } ?: previousDestination.value
        }

    fun updateCoreGraphState() {
        val currentDestination = coreNavController.currentDestination
        val parentGraph = currentDestination?.parent

        val coreRootRoute = CoreRoot::class.qualifiedName
            ?: CoreRoot::class.simpleName
            ?: "CoreRoot"

        _isInCoreGraph.value = parentGraph?.route == coreRootRoute
    }

    fun isSelectedBottomBarDestination(destination: BottomBarDestinations): Boolean{
        val currentDestination = coreNavController.currentDestination
        val parentGraph = currentDestination?.parent

        return parentGraph?.route == destination.route.qualifiedName
                || parentGraph?.route == destination.route.simpleName
                || parentGraph?.route == destination.baseRoute.qualifiedName
                || parentGraph?.route == destination.baseRoute.simpleName
    }

    fun navigateToBottomBarDestination(bottomBarDestination: BottomBarDestinations) {
        trace("Navigation: ${bottomBarDestination.route}"){
            val bottomBarNavigationOptions = navOptions {
                popUpTo(CoreRoot) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
            when (bottomBarDestination) {
                BottomBarDestinations.VehicleListDest -> coreNavController.navigateToVehicleList(bottomBarNavigationOptions)
                BottomBarDestinations.FavouriteDest -> coreNavController.navigateToFavourites(bottomBarNavigationOptions)
                BottomBarDestinations.CurrentTrackingDest -> coreNavController.navigateToTracking(bottomBarNavigationOptions)
                BottomBarDestinations.HistoryDest -> coreNavController.navigateToHistory(bottomBarNavigationOptions)
                BottomBarDestinations.MenuDest -> coreNavController.navigateToSettings(bottomBarNavigationOptions)
            }
        }
    }

}