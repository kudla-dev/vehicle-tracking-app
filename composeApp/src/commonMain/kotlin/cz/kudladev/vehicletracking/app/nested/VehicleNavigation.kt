package cz.kudladev.vehicletracking.app.nested

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import cz.kudladev.vehicletracking.app.AppState
import cz.kudladev.vehicletracking.app.core.sharedKoinViewModel
import cz.kudladev.vehicletracking.search.SearchScreenRoot
import cz.kudladev.vehicletracking.vehicle_list.VehicleListScreenRoot
import cz.kudladev.vehicletracking.vehicle_list.VehicleListViewModel
import kotlinx.serialization.Serializable

@Serializable
data class VehicleRoot(val searchQuery: String? = null)
@Serializable
data class Search(val type: String)


@Composable
fun VehicleNavigation(
    appState: AppState
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = VehicleRoot()
    ) {
        composable<VehicleRoot> {
            val vehicleListViewModel = it.sharedKoinViewModel<VehicleListViewModel>(appState.coreNavController)
            val searchQuery = it.toRoute<VehicleRoot>().searchQuery
            VehicleListScreenRoot(
                vehicleListViewModel = vehicleListViewModel,
                paddingValues = appState.paddingValues,
                onSearch = { type ->
                    navController.navigate(Search(type))
                },
                searchQuery = searchQuery
            )
        }
        composable<Search> { backStackEntry ->
            val type = backStackEntry.toRoute<Search>().type
            SearchScreenRoot(
                paddingValues = appState.paddingValues,
                searchType = type,
                onCancel = {
                    navController.navigateUp()
                },
                onSearch = {
                    navController.navigate(
                        VehicleRoot(
                            searchQuery = it
                        )
                    ){
                        popUpTo(VehicleRoot()) {
                            inclusive = false
                        }
                    }
                }
            )
        }
    }
}