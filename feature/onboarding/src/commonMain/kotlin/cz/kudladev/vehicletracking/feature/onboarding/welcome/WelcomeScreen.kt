package cz.kudladev.vehicletracking.feature.onboarding.welcome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cz.kudladev.vehicletracking.feature.onboarding.welcome.components.AuthSection
import kotlinx.serialization.Serializable

@Serializable
data object Welcome

@Composable
fun WelcomeScreenRoot(
    onRegister: () -> Unit,
    onLogin: () -> Unit
){

    WelcomeScreen(
        onRegister = onRegister,
        onLogin = onLogin
    )
}

@Composable
private fun WelcomeScreen(
    onRegister: () -> Unit,
    onLogin: () -> Unit
){
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.weight(1f)
        )
        AuthSection(
            onRegister = onRegister,
            onLogin = onLogin
        )
    }
}