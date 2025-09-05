package cz.kudladev.vehicletracking.app

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.BottomAppBarScrollBehavior
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.util.trace
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import cz.kudladev.vehicletracking.app.navigation.BottomBarDestinations
import cz.kudladev.vehicletracking.app.navigation.core.*
import cz.kudladev.vehicletracking.core.designsystem.snackbar.StackedSnackbarAnimation
import cz.kudladev.vehicletracking.core.designsystem.snackbar.StackedSnackbarDuration
import cz.kudladev.vehicletracking.core.designsystem.snackbar.StackedSnakbarHostState
import cz.kudladev.vehicletracking.core.designsystem.snackbar.rememberStackedSnackbarHostState
import cz.kudladev.vehicletracking.core.domain.auth.UserStateHolder
import cz.kudladev.vehicletracking.model.Snackbar
import cz.kudladev.vehicletracking.model.SnackbarLength
import cz.kudladev.vehicletracking.model.SnackbarStyle
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.compose.koinInject


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberAppState(
    navController: NavHostController = rememberNavController(),
    userStateHolder: UserStateHolder = koinInject<UserStateHolder>()
): AppState {

    val snackbarHostState = rememberStackedSnackbarHostState(
        maxStack = 1,
        animation = StackedSnackbarAnimation.Slide
    )

    val hazeState = rememberHazeState()

    val bottomAppBarScrollBehavior = BottomAppBarDefaults.exitAlwaysScrollBehavior()

    return remember(
        navController,
        userStateHolder
    ) {
        AppState(
            coreNavController = navController,
            userStateHolder = userStateHolder,
            snackbarHost = snackbarHostState,
            hazeState = hazeState,
            bottomAppBarScrollBehavior = bottomAppBarScrollBehavior
        )
    }
}

class AppState @OptIn(ExperimentalMaterial3Api::class) constructor(
    val coreNavController: NavHostController,
    val userStateHolder: UserStateHolder,
    val snackbarHost: StackedSnakbarHostState,
    val hazeState: HazeState,
    val bottomAppBarScrollBehavior: BottomAppBarScrollBehavior
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

    fun isSelectedBottomBarDestination(destination: BottomBarDestinations): Boolean {
        val currentDestination = coreNavController.currentBackStackEntry?.destination ?: return false

        // Check if any destination in the hierarchy matches our target destination route
        return currentDestination.hierarchy.any { hierarchyDestination ->
            val hierarchyRoute = hierarchyDestination.route

            println("Hierarchy route: $hierarchyRoute, checking against ${destination.route}")

            hierarchyRoute == destination.route.qualifiedName ||
                    hierarchyRoute == destination.route.simpleName ||
                    hierarchyRoute == destination.baseRoute.qualifiedName ||
                    hierarchyRoute == destination.baseRoute.simpleName
        }
    }

    fun navigateToBottomBarDestination(bottomBarDestination: BottomBarDestinations) {
        trace("Navigation: ${bottomBarDestination.route}"){
            val isCurrentDestination = isSelectedBottomBarDestination(bottomBarDestination)

            println("isCurrentDestination: $isCurrentDestination")

            val navigationOptions = if (isCurrentDestination) {
                // If clicking the same destination, refresh by not saving/restoring state
                navOptions {
                    popUpTo(CoreRoot) {
                        saveState = false
                    }
                    launchSingleTop = true
                    restoreState = false
                }
            } else {
                // Normal navigation to a different destination
                navOptions {
                    popUpTo(CoreRoot) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }

            when (bottomBarDestination) {
                BottomBarDestinations.VehicleListDest -> coreNavController.navigateToVehicleList(navigationOptions)
                BottomBarDestinations.FavouriteDest -> coreNavController.navigateToFavourites(navigationOptions)
                BottomBarDestinations.CurrentTrackingDest -> coreNavController.navigateToTracking(navigationOptions)
                BottomBarDestinations.HistoryDest -> coreNavController.navigateToHistory(navigationOptions)
                BottomBarDestinations.MenuDest -> coreNavController.navigateToSettings(navigationOptions)
            }
        }
    }

    fun navigateToCurrentTrackingRoot(){
        trace("Navigation: CurrentTrackingRoot") {
            coreNavController.navigateToTracking(navOptions {
                popUpTo(CoreRoot) {
                    saveState = false
                }
                launchSingleTop = true
                restoreState = false
            })
        }
    }

    fun showSnackbar(
        event: Snackbar
    ){
        val snackbarLength = when (event.snackbarLength) {
            SnackbarLength.SHORT -> StackedSnackbarDuration.Short
            SnackbarLength.LONG -> StackedSnackbarDuration.Long
            SnackbarLength.INDEFINITE -> StackedSnackbarDuration.Indefinite
        }
        when (event.snackbarStyle) {
            SnackbarStyle.INFO -> {
                snackbarHost.showInfoSnackbar(
                    title = event.title,
                    description = event.description,
                    actionTitle = event.actionTitle,
                    action = event.action,
                    duration = snackbarLength
                )
            }
            SnackbarStyle.WARNING -> {
                snackbarHost.showWarningSnackbar(
                    title = event.title,
                    description = event.description,
                    actionTitle = event.actionTitle,
                    action = event.action,
                    duration = snackbarLength
                )
            }
            SnackbarStyle.SUCCESS -> {
                snackbarHost.showSuccessSnackbar(
                    title = event.title,
                    description = event.description,
                    actionTitle = event.actionTitle,
                    action = event.action,
                    duration = snackbarLength
                )
            }
            SnackbarStyle.ERROR -> {
                snackbarHost.showErrorSnackbar(
                    title = event.title,
                    description = event.description,
                    actionTitle = event.actionTitle,
                    action = event.action,
                    duration = snackbarLength
                )
            }
        }
    }

}