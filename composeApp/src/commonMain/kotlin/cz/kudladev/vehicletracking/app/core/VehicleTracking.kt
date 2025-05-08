package cz.kudladev.vehicletracking.app.core

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import cz.kudladev.vehicletracking.app.AppState
import cz.kudladev.vehicletracking.core.presentation.components.basics.BottomBar
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun VehicleTracking(
    appState: AppState
){
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { BottomBar(appState) }
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