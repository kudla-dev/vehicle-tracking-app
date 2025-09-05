package cz.kudladev.vehicletracking.app.navigation.nested

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import cz.kudladev.vehicletracking.app.AppState
import cz.kudladev.vehicletracking.app.navigation.core.sharedKoinViewModel
import cz.kudladev.vehicletracking.feature.favourite.Favourites
import cz.kudladev.vehicletracking.feature.favourite.FavouritesScreenRoot
import cz.kudladev.vehicletracking.feature.history.History
import cz.kudladev.vehicletracking.feature.history.HistoryScreenRoot
import cz.kudladev.vehicletracking.feature.menu.admin_settings.AdminSettings
import cz.kudladev.vehicletracking.feature.menu.admin_settings.AdminSettingsRoot
import cz.kudladev.vehicletracking.feature.menu.main.MenuScreenRoot
import cz.kudladev.vehicletracking.feature.menu.manage_trackings.ManageTrackings
import cz.kudladev.vehicletracking.feature.menu.manage_trackings.ManageTrackingsRoot
import cz.kudladev.vehicletracking.feature.menu.manage_trackings.ManageTrackingsTypes
import cz.kudladev.vehicletracking.feature.menu.manage_vehicles.AddEditVehicleRoot
import cz.kudladev.vehicletracking.feature.menu.manage_vehicles.ManageVehiclesAddEdit
import cz.kudladev.vehicletracking.feature.menu.protocols.Protocols
import cz.kudladev.vehicletracking.feature.menu.protocols.ProtocolsRoot
import cz.kudladev.vehicletracking.feature.menu.protocols.ProtocolsType
import cz.kudladev.vehicletracking.feature.menu.tracking_detail.TrackingDetail
import cz.kudladev.vehicletracking.feature.menu.tracking_detail.TrackingDetailRoot
import cz.kudladev.vehicletracking.feature.search.SearchScreenRoot
import cz.kudladev.vehicletracking.feature.tracking.Tracking
import cz.kudladev.vehicletracking.feature.tracking.TrackingScreenRoot
import cz.kudladev.vehicletracking.feature.vehicledetail.VehicleDetailRoot
import cz.kudladev.vehicletracking.feature.vehicledetail.VehicleDetails
import cz.kudladev.vehicletracking.feature.vehicles.VehicleList
import cz.kudladev.vehicletracking.feature.vehicles.VehicleListScreenRoot
import cz.kudladev.vehicletracking.feature.vehicles.VehicleListViewModel
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

@Serializable
data object Menu

@Serializable
data class Search(
    val type: String = "vehicle",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuNavigation(
    appState: AppState,
    startDestination: KClass<*>
){
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = startDestination
    ){
         composable<Menu>{
            MenuScreenRoot(
                paddingValues = appState.paddingValues,
                bottomAppBarScrollBehavior = appState.bottomAppBarScrollBehavior,
                onAdminSettings = {
                    navController.navigate(AdminSettings)
                },
                onManageVehicles = {
                    navController.navigate(VehicleList(
                        searchQuery = null,
                        create = true
                    ))
                },
                onActiveTrackings = {
                    navController.navigate(ManageTrackings(ManageTrackingsTypes.ACTIVE))
                },
                onNonStartedTrackings = {
                    navController.navigate(ManageTrackings(ManageTrackingsTypes.NOT_STARTED))
                },
                onNewRequestsTrackings = {
                    navController.navigate(ManageTrackings(ManageTrackingsTypes.REQUESTED))
                },
                onTrackingHistory = {
                    navController.navigate(ManageTrackings(ManageTrackingsTypes.HISTORY))
                }
            )
        }
        composable<Tracking> {
            TrackingScreenRoot(
                paddingValues = appState.paddingValues,
                scrollBehaviour = appState.bottomAppBarScrollBehavior,
                onVehicleClick = { vehicleId ->
                    navController.navigate(
                        VehicleDetails(vehicleId = vehicleId)
                    )
                }
            )
        }
        composable<AdminSettings> {
            AdminSettingsRoot(
                paddingValues = appState.paddingValues,
                bottomAppBarScrollBehavior = appState.bottomAppBarScrollBehavior,
                onBack = {
                    navController.navigateUp()
                }
            )
        }
        composable<ManageVehiclesAddEdit> {
            AddEditVehicleRoot(
                paddingValues = appState.paddingValues,
                bottomAppBarScrollBehavior = appState.bottomAppBarScrollBehavior,
                onBack = {
                    navController.navigateUp()
                }
            )
        }
        composable<ManageTrackings> {
            val type = it.toRoute<ManageTrackings>().type
            ManageTrackingsRoot(
                paddingValues = appState.paddingValues,
                bottomAppBarScrollBehavior = appState.bottomAppBarScrollBehavior,
                type = type,
                onBack = {
                    navController.navigateUp()
                },
                onTrackingClicked = { tracking ->
                    navController.navigate(TrackingDetail(trackingId = tracking.id))
                }
            )
        }
        composable<TrackingDetail> {
            TrackingDetailRoot(
                paddingValues = appState.paddingValues,
                bottomAppBarScrollBehavior = appState.bottomAppBarScrollBehavior,
                onBack = {
                    navController.navigateUp()
                },
                onPickUpProtocol = { trackingId, trackingState ->
                    navController.navigate(Protocols(type = ProtocolsType.PICKUP, trackingId = trackingId, trackingState = trackingState))
                },
                onReturnProtocol = { trackingId, trackingState ->
                    navController.navigate(Protocols(type = ProtocolsType.RETURN, trackingId = trackingId, trackingState = trackingState))
                }
            )
        }
        composable<History> {
            HistoryScreenRoot(
                paddingValues = appState.paddingValues,
                bottomAppBarScrollBehavior = appState.bottomAppBarScrollBehavior,
            )
        }
        composable<Favourites> {
            FavouritesScreenRoot(
                paddingValues = appState.paddingValues,
                bottomAppBarScrollBehavior = appState.bottomAppBarScrollBehavior,
            )
        }
        composable<Protocols> {
            val type = it.toRoute<Protocols>().type
            ProtocolsRoot(
                type = type,
                paddingValues = appState.paddingValues,
                bottomAppBarScrollBehavior = appState.bottomAppBarScrollBehavior,
                onBack = {
                    navController.navigateUp()
                }
            )
        }
        composable<VehicleList> {
            val vehicleListViewModel = it.sharedKoinViewModel<VehicleListViewModel>(appState.coreNavController)
            val searchQuery = it.toRoute<VehicleList>().searchQuery
            val create = it.toRoute<VehicleList>().create
            VehicleListScreenRoot(
                vehicleListViewModel = vehicleListViewModel,
                paddingValues = appState.paddingValues,
                bottomAppBarScrollBehavior = appState.bottomAppBarScrollBehavior,
                onSearch = { type ->
                    navController.navigate(Search(type))
                },
                searchQuery = searchQuery,
                onVehicleClick = { vehicleId ->
                    navController.navigate(
                        VehicleDetails(vehicleId = vehicleId)
                    ) {
                        popUpTo(VehicleList()) {
                            inclusive = false
                        }
                    }
                },
                onCreate = if (create){
                    {
                        navController.navigate(ManageVehiclesAddEdit){
                            popUpTo(VehicleList()) {
                                inclusive = false
                            }
                        }
                    }
                } else {
                    null
                }
            )
        }
        composable<VehicleDetails> {
            VehicleDetailRoot(
                paddingValues = appState.paddingValues,
                bottomAppBarScrollBehavior = appState.bottomAppBarScrollBehavior,
                onBack = { navController.navigateUp() },
                onCreate = {
                    appState.navigateToCurrentTrackingRoot()
                }
            )
        }
        composable<Search> { backStackEntry ->
            val type = backStackEntry.toRoute<Search>().type
            SearchScreenRoot(
                paddingValues = appState.paddingValues,
                bottomAppBarScrollBehavior = appState.bottomAppBarScrollBehavior,
                searchType = type,
                onCancel = {
                    navController.navigateUp()
                },
                onSearch = {
                    navController.navigate(
                        VehicleList(
                            searchQuery = it
                        )
                    )
                },
                onVehicleSelected = { vehicleId ->
                    navController.navigate(
                        VehicleDetails(vehicleId = vehicleId)
                    ) {
                        popUpTo(VehicleList()) {
                            inclusive = false
                        }
                    }
                }
            )
        }
    }
}