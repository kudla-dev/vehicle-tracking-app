package cz.kudladev.vehicletracking.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import cz.kudladev.vehicletracking.auth.domain.UserStateHolder
import org.koin.compose.koinInject
import kotlin.reflect.KClass


@Composable
fun rememberAppState(
    navHostController: NavHostController,
    userStateHolder: UserStateHolder = koinInject<UserStateHolder>()
): AppState = remember(navHostController) {
    AppState(
        navHostController,
        userStateHolder
    )
}

class AppState(
    val navHostController: NavHostController,
    private val userStateHolder: UserStateHolder
) {

    val shouldShowBottomBar: Boolean = true



}