package cz.kudladev.vehicletracking.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import cz.kudladev.vehicletracking.app.AppState




@Composable
fun VehicleTracking(
    appState: AppState
){
    NavHost(
        navController = appState.navHostController,
        startDestination = AuthRoot
    ){
        authNavigation(appState)
    }
}