package cz.kudladev.vehicletracking.app.navigation.core

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import cz.kudladev.vehicletracking.app.AppState
import cz.kudladev.vehicletracking.app.navigation.BottomBar
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun VehicleTracking(
    appState: AppState
){

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // Only show BottomBar when in core graph
            if (appState.isInCoreGraph.collectAsState(initial = false).value) {
                BottomBar(appState)
            }
        },
        snackbarHost = { SnackbarHost(appState.snackbarHostState) },
        contentWindowInsets = WindowInsets()
    ) {
        appState.paddingValues = it
        NavHost(
            navController = appState.coreNavController,
            startDestination = AuthRoot
        ){
            authNavigation(appState)
            coreNavigation(appState)
        }
    }
}


/**
 * Extension to get a ViewModel scoped to a navigation graph.
 * This ensures that ViewModels persist across navigation within the same graph.
 */
@Composable
inline fun <reified T: ViewModel> NavBackStackEntry.sharedKoinViewModel(
    navController: NavController
): T {
    val navGraphRoute = destination.parent?.route ?: return koinViewModel<T>()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return koinViewModel(
        viewModelStoreOwner = parentEntry
    )
}

/**
 * Extension to get a ViewModel scoped to a specific navigation route.
 * This allows sharing ViewModels between specific screens.
 */
@Composable
inline fun <reified T: ViewModel> NavBackStackEntry.sharedKoinViewModel(
    navController: NavController,
    route: Any
): T {
    val stringRoute = when (route) {
        is String -> route
        else -> route::class.qualifiedName ?: route::class.simpleName ?: route.toString()
    }

    val parentEntry = remember(this) {
        navController.getBackStackEntry(stringRoute)
    }

    return koinViewModel(
        viewModelStoreOwner = parentEntry
    )
}