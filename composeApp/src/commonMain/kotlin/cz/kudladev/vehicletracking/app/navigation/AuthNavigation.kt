package cz.kudladev.vehicletracking.app.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import cz.kudladev.vehicletracking.app.AppState
import cz.kudladev.vehicletracking.auth.presentation.loading.LoadingScreenRoot
import cz.kudladev.vehicletracking.auth.presentation.login.LoginScreenRoot
import cz.kudladev.vehicletracking.auth.presentation.register.RegisterScreenRoot
import cz.kudladev.vehicletracking.auth.presentation.welcome.WelcomeScreenRoot
import kotlinx.serialization.Serializable

@Serializable
data object AuthRoot
@Serializable
data object Loading
@Serializable
data object Welcome
@Serializable
data object Login
@Serializable
data object Register

fun NavGraphBuilder.authNavigation(
    appState: AppState
){
    navigation<AuthRoot>(
        startDestination = Loading
    ) {
        composable<Loading> {
            LoadingScreenRoot(
                userStateHolder = appState.userStateHolder,
                onAuth = {
                    appState.navHostController.navigate(CoreRoot){
                        popUpTo(AuthRoot) {
                            inclusive = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNonAuth = {
                    appState.navHostController.navigate(Welcome) {
                        popUpTo(AuthRoot) {
                            inclusive = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        composable<Welcome> {
            WelcomeScreenRoot(
                onRegister = {
                    appState.navHostController.navigate(Register)
                },
                onLogin = {
                    appState.navHostController.navigate(Login)
                }
            )
        }
        composable<Register> {
            RegisterScreenRoot(
                onBack = {
                    appState.navHostController.navigateUp()
                },
                onRegisterConfirmed = {
                    appState.navHostController.navigate(CoreRoot) {
                        popUpTo(AuthRoot) {
                            inclusive = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        composable<Login> {
            LoginScreenRoot(
                onBack = {
                    appState.navHostController.navigateUp()
                },
                onLoginConfirm = {
                    appState.navHostController.navigate(CoreRoot) {
                        popUpTo(AuthRoot) {
                            inclusive = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}