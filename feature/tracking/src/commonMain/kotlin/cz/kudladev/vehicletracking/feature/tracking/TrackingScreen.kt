package cz.kudladev.vehicletracking.feature.tracking

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import cz.kudladev.vehicletracking.core.designsystem.LargeTopAppBar

@Composable
fun TrackingScreenRoot(
    paddingValues: PaddingValues,
) {

    TrackingScreen(
        paddingValues = paddingValues,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackingScreen(
    paddingValues: PaddingValues,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text("Current Tracking")
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) {

    }

}