package cz.kudladev.vehicletracking.app.navigation.nested

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import cz.kudladev.vehicletracking.app.AppState
import cz.kudladev.vehicletracking.app.navigation.core.sharedKoinViewModel
import cz.kudladev.vehicletracking.feature.search.SearchScreenRoot
import cz.kudladev.vehicletracking.feature.vehicledetail.VehicleDetailRoot
import cz.kudladev.vehicletracking.feature.vehicledetail.VehicleDetails
import cz.kudladev.vehicletracking.feature.vehicles.VehicleListScreenRoot
import cz.kudladev.vehicletracking.feature.vehicles.VehicleListViewModel
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
                searchQuery = searchQuery,
                onVehicleClick = { vehicleId ->
                    navController.navigate(
                        VehicleDetails(vehicleId = vehicleId)
                    ) {
                        popUpTo(VehicleRoot()) {
                            inclusive = false
                        }
                    }
                }
            )
        }
        composable<VehicleDetails> {
            VehicleDetailRoot(
                paddingValues = appState.paddingValues,
                onBack = { navController.navigateUp() },
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