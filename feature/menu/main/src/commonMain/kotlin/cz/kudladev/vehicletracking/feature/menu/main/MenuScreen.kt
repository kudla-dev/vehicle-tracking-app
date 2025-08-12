package cz.kudladev.vehicletracking.feature.menu.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.Logout
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.twotone.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.kudladev.vehicletracking.core.designsystem.LargeTopAppBar
import cz.kudladev.vehicletracking.core.domain.auth.UserStateHolder
import cz.kudladev.vehicletracking.core.ui.menu.MenuSection
import cz.kudladev.vehicletracking.core.ui.menu.MenuSectionItem
import cz.kudladev.vehicletracking.core.ui.menu.Version
import cz.kudladev.vehicletracking.core.ui.user.UserCard
import cz.kudladev.vehicletracking.core.ui.user.UserNotLoggedIn
import cz.kudladev.vehicletracking.model.Role
import cz.kudladev.vehicletracking.model.User
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import vehicletracking.feature.menu.main.generated.resources.Res
import vehicletracking.feature.menu.main.generated.resources.accountSection
import vehicletracking.feature.menu.main.generated.resources.adminSection
import vehicletracking.feature.menu.main.generated.resources.adminSettings
import vehicletracking.feature.menu.main.generated.resources.changePassword
import vehicletracking.feature.menu.main.generated.resources.logout
import vehicletracking.feature.menu.main.generated.resources.manageUsers
import vehicletracking.feature.menu.main.generated.resources.manageVehicles
import vehicletracking.feature.menu.main.generated.resources.menuTitle
import vehicletracking.feature.menu.main.generated.resources.personalData
import vehicletracking.feature.menu.main.generated.resources.trackingHistory
import vehicletracking.feature.menu.main.generated.resources.trackingsActive
import vehicletracking.feature.menu.main.generated.resources.trackingsNewRequest
import vehicletracking.feature.menu.main.generated.resources.trackingsReadyToStart
import vehicletracking.feature.menu.main.generated.resources.trackingsSection

@Composable
fun MenuScreenRoot(
    paddingValues: PaddingValues,
    userStateHolder: UserStateHolder = koinInject(),
    onAdminSettings: () -> Unit,
    onManageVehicles: () -> Unit,
    onActiveTrackings: () -> Unit,
    onNonStartedTrackings: () -> Unit,
    onNewRequestsTrackings: () -> Unit,
    onTrackingHistory: () -> Unit,
) {
    val user = userStateHolder.user.collectAsStateWithLifecycle().value

    MenuScreen(
        paddingValues = paddingValues,
        user = user,
        onAdminSettings = onAdminSettings,
        onManageVehicles = onManageVehicles,
        onActiveTrackings = onActiveTrackings,
        onNonStartedTrackings = onNonStartedTrackings,
        onNewRequestsTrackings = onNewRequestsTrackings,
        onTrackingHistory = onTrackingHistory,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MenuScreen(
    paddingValues: PaddingValues,
    user: User?,
    onAdminSettings: () -> Unit,
    onManageVehicles: () -> Unit,
    onActiveTrackings: () -> Unit,
    onNonStartedTrackings: () -> Unit,
    onNewRequestsTrackings: () -> Unit,
    onTrackingHistory: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        stringResource(Res.string.menuTitle),
                        fontStyle = FontStyle.Italic

                    )
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        val padding = PaddingValues(
            start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
            end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
            top = innerPadding.calculateTopPadding() + 16.dp,
            bottom = paddingValues.calculateBottomPadding() + 16.dp,
        )
        AnimatedVisibility(
            visible = user == null,
        ){
            UserNotLoggedIn(modifier = Modifier.padding(padding))
            return@AnimatedVisibility
        }
        AnimatedVisibility(
            visible = user != null,
        ){
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = padding,
            ) {
                item {
                    UserCard(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        user = user!!,
                        onClick = {

                        }
                    )
                }
                if (user!!.role == Role.ADMIN){
                    adminSection(
                        onManageVehicles = onManageVehicles,
                        onAdminSettings = onAdminSettings,
                        onTrackingHistory = onTrackingHistory,
                    )
                }
                if (user!!.role == Role.ADMIN){
                    trackingSection(
                        onActiveTrackings = onActiveTrackings,
                        onNonStartedTrackings= onNonStartedTrackings,
                        onNewRequestsTrackings = onNewRequestsTrackings
                    )
                }
                accountSection()
                versionSection()
            }
        }
    }
}

private fun LazyListScope.versionSection() {
    item {
        Version(
            version = "1.0.0",
            versionDate = "6.5.2025"
        )
    }
}

private fun LazyListScope.accountSection() {
    item {
        MenuSection(
            title = stringResource(Res.string.accountSection)
        ) {
            MenuSectionItem(
                icon = Icons.TwoTone.ManageAccounts,
                title = stringResource(Res.string.personalData),
                onClick = {

                }
            )
            MenuSectionItem(
                icon = Icons.TwoTone.Lock,
                title = stringResource(Res.string.changePassword),
                onClick = {

                }
            )
            MenuSectionItem(
                icon = Icons.AutoMirrored.TwoTone.Logout,
                title = stringResource(Res.string.logout),
                onClick = {
                    // Handle logout
                },
                isLast = true
            )
        }
    }
}

private fun LazyListScope.adminSection(
    onManageVehicles: () -> Unit,
    onAdminSettings: () -> Unit,
    onTrackingHistory: () -> Unit,
) {
    item {
        MenuSection(
            title = stringResource(Res.string.adminSection)
        ) {
            MenuSectionItem(
                icon = Icons.TwoTone.Groups,
                title = stringResource(Res.string.manageUsers),
                onClick = {

                }
            )
            MenuSectionItem(
                icon = Icons.TwoTone.DirectionsCar,
                title = stringResource(Res.string.manageVehicles),
                onClick = {
                    onManageVehicles()
                },
            )
            MenuSectionItem(
                icon = Icons.TwoTone.TrackChanges,
                title = stringResource(Res.string.trackingHistory),
                onClick = {
                    onTrackingHistory()
                },
            )
            MenuSectionItem(
                icon = Icons.TwoTone.AdminPanelSettings,
                title = stringResource(Res.string.adminSettings),
                onClick = onAdminSettings,
                isLast = true
            )
        }
    }
}

private fun LazyListScope.trackingSection(
    onActiveTrackings: () -> Unit,
    onNonStartedTrackings: () -> Unit,
    onNewRequestsTrackings: () -> Unit,
) {
    item {
        MenuSection(
            title = stringResource(Res.string.trackingsSection)
        ) {
            MenuSectionItem(
                icon = Icons.TwoTone.MarkunreadMailbox,
                title = stringResource(Res.string.trackingsNewRequest),
                onClick = {
                    onNewRequestsTrackings()
                },
            )
            MenuSectionItem(
                icon = Icons.TwoTone.SportsScore,
                title = stringResource(Res.string.trackingsReadyToStart),
                onClick = {
                    onNonStartedTrackings()
                }
            )
            MenuSectionItem(
                icon = Icons.TwoTone.Motorcycle,
                title = stringResource(Res.string.trackingsActive),
                onClick = {
                    onActiveTrackings()
                },
                isLast = true
            )
        }
    }
}

