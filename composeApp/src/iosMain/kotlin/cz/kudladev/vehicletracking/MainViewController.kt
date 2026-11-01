package cz.kudladev.vehicletracking

import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() = ComposeUIViewController {
    if (iosAppConfig.environment == cz.kudladev.vehicletracking.network.AppEnvironment.MOCK) MockUnavailable() else App()
}