package cz.kudladev.vehicletracking.core.presentation.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import cz.kudladev.vehicletracking.app.AppState
import cz.kudladev.vehicletracking.app.navigation.CoreRoot
import cz.kudladev.vehicletracking.app.navigation.History
import cz.kudladev.vehicletracking.app.navigation.Settings
import cz.kudladev.vehicletracking.app.navigation.Tracking
import cz.kudladev.vehicletracking.app.navigation.VehicleList
import kotlin.reflect.KClass

enum class BottomBarDestinations(
    val unSelectedIcon: ImageVector,
    val selectedIcon: ImageVector,
    val label: String,
    val route: KClass<*>,
    val baseRoute: KClass<*> = route,
){
    VehicleListDest(
        unSelectedIcon = Icons.AutoMirrored.Default.List,
        selectedIcon = Icons.AutoMirrored.Filled.List,
        label = "Vehicles",
        route = VehicleList::class,
    ),
    CurrentTrackingDest(
        unSelectedIcon = Icons.Outlined.DirectionsCar,
        selectedIcon = Icons.Filled.DirectionsCar,
        label = "Tracking",
        route = Tracking::class,
    ),
    HistoryDest(
        unSelectedIcon = Icons.Outlined.History,
        selectedIcon = Icons.Filled.History,
        label = "History",
        route = History::class,
    ),
    SettingsDest(
        unSelectedIcon = Icons.Outlined.Settings,
        selectedIcon = Icons.Filled.Settings,
        label = "Settings",
        route = Settings::class,
    ),
}


@Composable
fun BottomBar(
    appState: AppState
){
    val currentDestination = appState.currentDestination
    val showBottomBar = appState.isInCoreGraph.collectAsStateWithLifecycle().value

    val selectedRoutes = remember(currentDestination) {
        BottomBarDestinations.entries.associateWith { destination ->
            currentDestination.isRouteInHierarchy(destination.baseRoute)
        }
    }

    if (showBottomBar){
        NavigationBar {
            BottomBarDestinations.entries.forEach { destination ->
                val isSelected = selectedRoutes[destination] ?: false
                BottomBarItem(
                    destination = destination,
                    isSelected = isSelected,
                    onClick = {
                       appState.navigateToBottomBarDestination(destination)
                    }
                )
            }
        }
    }
}


@Composable
fun RowScope.BottomBarItem(
    destination: BottomBarDestinations,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    NavigationBarItem(
        selected = isSelected,
        onClick = onClick,
        icon = {
            Icon(
                imageVector = if (isSelected) destination.selectedIcon else destination.unSelectedIcon,
                contentDescription = destination.label
            )
        },
        label = {
            Text(
                text = destination.label
            )
        },

    )

}

private fun NavDestination?.isRouteInHierarchy(route: KClass<*>) =
    this?.hierarchy?.any {
        it.hasRoute(route)
    } ?: false