package cz.kudladev.vehicletracking.menu.admin_settings

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.HomeWork
import androidx.compose.material.icons.twotone.Mail
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.kudladev.vehicletracking.core.designsystem.BackButton
import cz.kudladev.vehicletracking.core.designsystem.LargeTopAppBar
import cz.kudladev.vehicletracking.menu.components.MenuSection
import cz.kudladev.vehicletracking.menu.components.MenuSectionItem
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AdminSettingsRoot(
    viewModel: AdminSettingsViewModel = koinViewModel(),
    paddingValues: PaddingValues,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    AdminSettingsScreen(
        paddingValues = paddingValues,
        state = state,
        onAction = viewModel::onAction,
        onBack = onBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSettingsScreen(
    paddingValues: PaddingValues,
    state: AdminSettingsState,
    onAction: (AdminSettingsAction) -> Unit,
    onBack: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text("Admin Settings")
                },
                navigationIcon = {
                    BackButton(
                        onClick = onBack
                    )
                },
                scrollBehavior = scrollBehavior,
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        val padding = PaddingValues(
            start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
            top = innerPadding.calculateTopPadding() + 16.dp,
            end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
            bottom = innerPadding.calculateBottomPadding() + 16.dp
        )
        LazyColumn(
            modifier = Modifier,
            contentPadding = padding,
        ) {
            item {
                MenuSection {
                    MenuSectionItem(
                        icon = Icons.TwoTone.HomeWork,
                        title = "Default place",
                        action = {

                        }
                    )
                    MenuSectionItem(
                        icon = Icons.TwoTone.Mail,
                        title = "Request notifications",
                        action = {

                        },
                        isLast = true
                    )
                }
            }
        }
    }
}
