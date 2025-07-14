package cz.kudladev.vehicletracking.feature.menu.manage_trackings

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import cz.kudladev.vehicletracking.core.designsystem.BackButton
import cz.kudladev.vehicletracking.core.designsystem.LargeTopAppBar
import cz.kudladev.vehicletracking.core.ui.tracking.TrackingItem
import cz.kudladev.vehicletracking.model.Tracking
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object ManageTrackings

@Composable
fun ManageTrackingsRoot(
    viewModel: ManageTrackingsViewModel = koinViewModel(),
    paddingValues: PaddingValues,
    onBack: () -> Unit,
    onTrackingClicked: (tracking: Tracking) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val trackings = viewModel.trackings.collectAsLazyPagingItems()

    ManageTrackingsScreen(
        paddingValues = paddingValues,
        state = state,
        trackings = trackings,
        onAction = viewModel::onAction,
        onBack = onBack,
        onTrackingClicked = onTrackingClicked,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ManageTrackingsScreen(
    paddingValues: PaddingValues,
    state: ManageTrackingsState,
    trackings: LazyPagingItems<Tracking>,
    onAction: (ManageTrackingsAction) -> Unit,
    onBack: () -> Unit,
    onTrackingClicked: (tracking: Tracking) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        "Active Trackings"
                    )
                },
                navigationIcon = {
                    BackButton { onBack() }
                },
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        val combinedPadding = PaddingValues(
            bottom = paddingValues.calculateBottomPadding() + 16.dp,
            top = innerPadding.calculateTopPadding() + 16.dp,
            start = innerPadding.calculateStartPadding(LocalLayoutDirection.current) + 16.dp,
            end = innerPadding.calculateEndPadding(LocalLayoutDirection.current) + 16.dp,
        )
        Crossfade(
            targetState = trackings.loadState.refresh
        ){
            when (it) {
                is LoadState.Error -> {
                    Text(
                        text = "Error loading trackings: ${it.error}",
                        modifier = Modifier.padding(combinedPadding)
                    )
                }
                LoadState.Loading -> {
                    Column(
                        modifier = Modifier.padding(combinedPadding)
                    ) {
                        CircularProgressIndicator()
                        Text(
                            text = "Loading trackings...",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                else -> {
                    when (trackings.itemCount == 0) {
                        true -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(combinedPadding),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = "No trackings",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(64.dp)
                                )
                                Text(
                                    text = "No trackings found",
                                    modifier = Modifier.padding(top = 8.dp),
                                    style = MaterialTheme.typography.titleSmall,
                                )
                            }
                        }
                        false -> {
                            LazyColumn(
                                contentPadding = combinedPadding,
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(trackings.itemCount) { tracking ->
                                    val item = trackings[tracking]
                                    if (item != null) {
                                        TrackingItem(
                                            tracking = item,
                                            onClick = { onTrackingClicked(item) },
                                            modifier = Modifier.padding(horizontal = 8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
