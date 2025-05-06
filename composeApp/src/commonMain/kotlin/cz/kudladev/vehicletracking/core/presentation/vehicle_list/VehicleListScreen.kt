package cz.kudladev.vehicletracking.core.presentation.vehicle_list

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
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
import cz.kudladev.vehicletracking.auth.domain.User
import cz.kudladev.vehicletracking.auth.domain.UserStateHolder
import cz.kudladev.vehicletracking.core.presentation.components.TopAppBar
import org.koin.compose.koinInject


@Composable
fun VehicleListScreenRoot(
    userState: UserStateHolder = koinInject(),
    paddingValues: PaddingValues
) {
    val user = userState.user.collectAsStateWithLifecycle()


    VehicleListScreen(
        paddingValues = paddingValues,
        user.value!!,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VehicleListScreen(
    paddingValues: PaddingValues,
    user: User
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Vehicle List",
                    )
                },
                scrollBehavior = scrollBehavior,
            )
        }
    ) { innerPadding ->
        Text(
            text = user.toString()
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                bottom = paddingValues.calculateBottomPadding(),
                top = innerPadding.calculateTopPadding(),
                start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                end = innerPadding.calculateEndPadding(LocalLayoutDirection.current)
            ),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            items(100) {
                Text(
                    text = "Item $it",
                )
            }
        }
    }

}