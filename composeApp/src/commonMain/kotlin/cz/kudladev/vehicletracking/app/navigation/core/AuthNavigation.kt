package cz.kudladev.vehicletracking.app.navigation.core

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import cz.kudladev.vehicletracking.app.AppState
import cz.kudladev.vehicletracking.feature.onboarding.loading.Loading
import cz.kudladev.vehicletracking.feature.onboarding.loading.LoadingScreenRoot
import cz.kudladev.vehicletracking.feature.onboarding.login.Login
import cz.kudladev.vehicletracking.feature.onboarding.login.LoginScreenRoot
import cz.kudladev.vehicletracking.feature.onboarding.register.Register
import cz.kudladev.vehicletracking.feature.onboarding.register.RegisterScreenRoot
import cz.kudladev.vehicletracking.feature.onboarding.welcome.Welcome
import cz.kudladev.vehicletracking.feature.onboarding.welcome.WelcomeScreenRoot
import kotlinx.serialization.Serializable

@Serializable
data object AuthRoot

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
                    appState.coreNavController.navigate(CoreRoot){
                        popUpTo(AuthRoot) {
                            inclusive = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNonAuth = {
                    appState.coreNavController.navigate(Welcome) {
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
                    appState.coreNavController.navigate(Register)
                },
                onLogin = {
                    appState.coreNavController.navigate(Login)
                }
            )
        }
        composable<Register> {
            RegisterScreenRoot(
                onBack = {
                    appState.coreNavController.navigateUp()
                },
                onRegisterConfirmed = {
                    appState.coreNavController.navigate(CoreRoot) {
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
                    appState.coreNavController.navigateUp()
                },
                onLoginConfirm = {
                    appState.coreNavController.navigate(CoreRoot) {
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