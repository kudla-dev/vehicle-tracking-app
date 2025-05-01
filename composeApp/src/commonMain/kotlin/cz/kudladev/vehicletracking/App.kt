package cz.kudladev.vehicletracking

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import cz.kudladev.vehicletracking.app.navigation.VehicleTracking
import cz.kudladev.vehicletracking.app.rememberAppState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {

    val appState = rememberAppState(
        navHostController = rememberNavController()
    )

    MaterialTheme {
        VehicleTracking(appState)
    }
}