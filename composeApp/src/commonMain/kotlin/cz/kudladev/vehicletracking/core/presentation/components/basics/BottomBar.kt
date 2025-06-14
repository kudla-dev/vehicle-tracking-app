package cz.kudladev.vehicletracking.core.presentation.components.basics

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import cz.kudladev.vehicletracking.app.AppState
import cz.kudladev.vehicletracking.app.navigation.core.Favourites
import cz.kudladev.vehicletracking.app.navigation.core.History
import cz.kudladev.vehicletracking.app.navigation.core.Menu
import cz.kudladev.vehicletracking.app.navigation.core.Tracking
import cz.kudladev.vehicletracking.app.navigation.core.VehicleList
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

enum class BottomBarDestinations(
    val unSelectedIcon: ImageVector,
    val selectedIcon: ImageVector,
    val label: String,
    @Serializable
    val route: KClass<*>,
    val baseRoute: KClass<*> = route,
){
    VehicleListDest(
        unSelectedIcon = Icons.AutoMirrored.Default.List,
        selectedIcon = Icons.AutoMirrored.Filled.List,
        label = "Vehicles",
        route = VehicleList::class,
    ),
    FavouriteDest(
        unSelectedIcon = Icons.Outlined.Favorite,
        selectedIcon = Icons.Filled.Favorite,
        label = "Favourite",
        route = Favourites::class,
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
    MenuDest(
        unSelectedIcon = Icons.Outlined.Menu,
        selectedIcon = Icons.Filled.Menu,
        label = "Menu",
        route = Menu::class,
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
                text = destination.label,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isSelected) {
                    FontWeight.Bold
                } else {
                    FontWeight.Normal
                },
            )
        },

    )

}

private fun NavDestination?.isRouteInHierarchy(route: KClass<*>) =
    this?.hierarchy?.any {
        it.hasRoute(route)
    } ?: false