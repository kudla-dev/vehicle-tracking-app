package cz.kudladev.vehicletracking.app

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.util.trace
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.navOptions
import cz.kudladev.vehicletracking.app.navigation.CoreRoot
import cz.kudladev.vehicletracking.app.navigation.navigateToHistory
import cz.kudladev.vehicletracking.app.navigation.navigateToSettings
import cz.kudladev.vehicletracking.app.navigation.navigateToTracking
import cz.kudladev.vehicletracking.app.navigation.navigateToVehicleList
import cz.kudladev.vehicletracking.auth.domain.UserStateHolder
import cz.kudladev.vehicletracking.core.presentation.components.BottomBarDestinations
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.compose.koinInject


@Composable
fun rememberAppState(
    navHostController: NavHostController,
    userStateHolder: UserStateHolder = koinInject<UserStateHolder>()
): AppState = remember(navHostController) {
    AppState(
        navHostController,
        userStateHolder
    )
}

class AppState(
    val navHostController: NavHostController,
    val userStateHolder: UserStateHolder
) {
    private val _isInCoreGraph = MutableStateFlow(false)
    val isInCoreGraph: StateFlow<Boolean> = _isInCoreGraph
    private val previousDestination = mutableStateOf<NavDestination?>(null)

    var paddingValues = PaddingValues()

    init {
        navHostController.addOnDestinationChangedListener { _, _, _ ->
            updateCoreGraphState()
        }
        updateCoreGraphState()
    }


    val currentDestination: NavDestination?
        @Composable
        get() {
            val currentEntry = navHostController.currentBackStackEntryFlow.collectAsState(initial = null)
            return currentEntry.value?.destination.also { destination ->
                if (destination != null) {
                    previousDestination.value = destination
                }
            } ?: previousDestination.value
        }

    fun updateCoreGraphState() {
        val currentDestination = navHostController.currentDestination
        val parentGraph = currentDestination?.parent

        val coreRootRoute = CoreRoot::class.qualifiedName
            ?: CoreRoot::class.simpleName
            ?: "CoreRoot"

        _isInCoreGraph.value = parentGraph?.route == coreRootRoute
    }

    fun isSelectedBottomBarDestination(destination: BottomBarDestinations): Boolean{
        val currentDestination = navHostController.currentDestination
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
                    inclusive = true
                }
                launchSingleTop = true
                restoreState = true
            }
            when (bottomBarDestination) {
                BottomBarDestinations.VehicleListDest -> navHostController.navigateToVehicleList(bottomBarNavigationOptions)
                BottomBarDestinations.CurrentTrackingDest -> navHostController.navigateToTracking(bottomBarNavigationOptions)
                BottomBarDestinations.HistoryDest -> navHostController.navigateToHistory(bottomBarNavigationOptions)
                BottomBarDestinations.SettingsDest -> navHostController.navigateToSettings(bottomBarNavigationOptions)
            }
        }
    }

}