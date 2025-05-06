package cz.kudladev.vehicletracking.app.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import cz.kudladev.vehicletracking.app.AppState
import cz.kudladev.vehicletracking.core.presentation.components.BottomBar

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
            navController = appState.navHostController,
            startDestination = AuthRoot
        ){
            authNavigation(appState)
            coreNavigation(appState)
        }
    }
}