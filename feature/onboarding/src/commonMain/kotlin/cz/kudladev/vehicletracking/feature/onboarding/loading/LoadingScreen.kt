package cz.kudladev.vehicletracking.feature.onboarding.loading

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.kudladev.vehicletracking.core.domain.auth.UserStateHolder
import cz.kudladev.vehicletracking.model.User
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object Loading

@Composable
fun LoadingScreenRoot(
    userStateHolder: UserStateHolder,
    viewModel: LoadingScreenViewModel = koinViewModel(),
    onAuth: () -> Unit,
    onNonAuth: () -> Unit,
){
    val state = viewModel.state.collectAsStateWithLifecycle().value
    val user = userStateHolder.user.collectAsStateWithLifecycle().value

    LaunchedEffect(state.loadingProcess) {
        println("Loading process: ${state.loadingProcess}")
        when (state.loadingProcess) {
            LoadingProcess.Loading -> {}
            LoadingProcess.Success -> {
                println("User: $user")
                onAuth()
            }
            is LoadingProcess.Error -> {
                onNonAuth()
            }
        }
    }

    LoadingScreen(
        user = user,
        state = state,
        onAuth = onAuth,
        onNonAuth = onNonAuth
    )
}

@Composable
private fun LoadingScreen(
    user: User?,
    state: LoadingScreenState,
    onAuth: () -> Unit,
    onNonAuth: () -> Unit,
){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
    }
}