package cz.kudladev.vehicletracking.app.navigation

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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import cz.kudladev.vehicletracking.app.AppState
import cz.kudladev.vehicletracking.app.navigation.core.*
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
        route = VehicleRoot::class,
    ),
    FavouriteDest(
        unSelectedIcon = Icons.Outlined.Favorite,
        selectedIcon = Icons.Filled.Favorite,
        label = "Favourite",
        route = FavouriteRoot::class,
    ),
    CurrentTrackingDest(
        unSelectedIcon = Icons.Outlined.DirectionsCar,
        selectedIcon = Icons.Filled.DirectionsCar,
        label = "Tracking",
        route = TrackingRoot::class,
    ),
    HistoryDest(
        unSelectedIcon = Icons.Outlined.History,
        selectedIcon = Icons.Filled.History,
        label = "History",
        route = HistoryRoot::class,
    ),
    MenuDest(
        unSelectedIcon = Icons.Outlined.Menu,
        selectedIcon = Icons.Filled.Menu,
        label = "Menu",
        route = MenuRoot::class,
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
        NavigationBar(
            modifier = Modifier
                .shadowWithClipIntersect(
                    elevation = 16.dp
                ),
            containerColor = MaterialTheme.colorScheme.background,
        ) {
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
        colors = NavigationBarItemColors(
            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
            selectedTextColor = MaterialTheme.colorScheme.onBackground,
            selectedIndicatorColor = MaterialTheme.colorScheme.primary,
            unselectedIconColor = MaterialTheme.colorScheme.onBackground,
            unselectedTextColor = MaterialTheme.colorScheme.onBackground,
            disabledIconColor = MaterialTheme.colorScheme.onBackground,
            disabledTextColor = MaterialTheme.colorScheme.onBackground,
        )

    )

}

private fun NavDestination?.isRouteInHierarchy(route: KClass<*>) =
    this?.hierarchy?.any {
        it.hasRoute(route)
    } ?: false


@Composable
fun Modifier.shadowWithClipIntersect(
    elevation: Dp,
    shape: Shape = RectangleShape,
    clip: Boolean = elevation > 0.dp,
    ambientColor: Color = DefaultShadowColor,
    spotColor: Color = DefaultShadowColor,
): Modifier {

    return this.then(
        Modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                clip = false, // Don't clip here
                ambientColor = ambientColor,
                spotColor = spotColor
            )
            // Then clip the content to the actual shape
            .drawWithCache {
                val path = Path().apply {
                    addOutline(shape.createOutline(size, layoutDirection, this@drawWithCache))
                }
                onDrawBehind {
                    clipPath(path, ClipOp.Intersect) {
                        // This is empty as we're just using it for clipping
                    }
                }
            }
    )
}