package cz.kudladev.vehicletracking.menu.root

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.twotone.AdminPanelSettings
import androidx.compose.material.icons.twotone.DirectionsCar
import androidx.compose.material.icons.twotone.Groups
import androidx.compose.material.icons.twotone.Lock
import androidx.compose.material.icons.twotone.ManageAccounts
import androidx.compose.material.icons.twotone.MarkunreadMailbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.kudladev.vehicletracking.auth.domain.Role
import cz.kudladev.vehicletracking.auth.domain.User
import cz.kudladev.vehicletracking.auth.domain.UserStateHolder
import cz.kudladev.vehicletracking.core.presentation.components.basics.TopAppBar
import cz.kudladev.vehicletracking.core.presentation.components.user.UserCard
import cz.kudladev.vehicletracking.core.presentation.components.user.UserNotLoggedIn
import cz.kudladev.vehicletracking.menu.components.MenuSection
import cz.kudladev.vehicletracking.menu.components.MenuSectionItem
import cz.kudladev.vehicletracking.menu.components.Version
import org.koin.compose.koinInject

@Composable
fun MenuScreenRoot(
    paddingValues: PaddingValues,
    userStateHolder: UserStateHolder = koinInject(),
    onAdminSettings: () -> Unit,
) {
    val user = userStateHolder.user.collectAsStateWithLifecycle().value

    MenuScreen(
        paddingValues = paddingValues,
        user = user,
        onAdminSettings = onAdminSettings,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MenuScreen(
     paddingValues: PaddingValues,
     user: User?,
     onAdminSettings: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text("Menu")
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
        if (user == null) {
            UserNotLoggedIn(modifier = Modifier.padding(padding))
            return@Scaffold
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = padding,
        ) {
            item {
                UserCard(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    user = user,
                    onClick = {

                    }
                )
            }
            if (user.role == Role.ADMIN){
                item {
                    MenuSection(
                        title = "Admin"
                    ) {
                        MenuSectionItem(
                            icon = Icons.TwoTone.Groups,
                            title = "Manage users",
                            onClick = {

                            }
                        )
                        MenuSectionItem(
                            icon = Icons.TwoTone.DirectionsCar,
                            title = "Manage vehicles",
                            onClick = {

                            },
                        )
                        MenuSectionItem(
                            icon = Icons.Default.TrackChanges,
                            title = "Tracking history",
                            onClick = {

                            },
                        )
                        MenuSectionItem(
                            icon = Icons.TwoTone.MarkunreadMailbox,
                            title = "New requests",
                            onClick = {

                            },
                        )
                        MenuSectionItem(
                            icon = Icons.TwoTone.AdminPanelSettings,
                            title = "Admin settings",
                            onClick = onAdminSettings,
                            isLast = true
                        )
                    }
                }
            }
            item {
                MenuSection(
                    title = "Account"
                ) {
                    MenuSectionItem(
                        icon = Icons.TwoTone.ManageAccounts,
                        title = "Personal data",
                        onClick = {

                        }
                    )
                    MenuSectionItem(
                        icon = Icons.TwoTone.Lock,
                        title = "Change password",
                        onClick = {

                        },
                        isLast = true
                    )
                }
            }
            item {
                Version(
                    version = "1.0.0",
                    versionDate = "6.5.2025"
                )
            }
        }
    }

}