package cz.kudladev.vehicletracking.feature.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomAppBarScrollBehavior
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.kudladev.vehicletracking.core.designsystem.theme.Images
import cz.kudladev.vehicletracking.core.ui.search.SearchQueryItem
import cz.kudladev.vehicletracking.core.ui.search.SearchResultItem
import cz.kudladev.vehicletracking.core.ui.search.SearchResultItemSkeleton
import cz.kudladev.vehicletracking.core.ui.search.SearchTextField
import cz.kudladev.vehicletracking.core.ui.searchString
import cz.kudladev.vehicletracking.model.UiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import vehicletracking.feature.search.generated.resources.Res
import vehicletracking.feature.search.generated.resources.searchDescription

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreenRoot(
    viewModel: SearchScreenViewModel = koinViewModel(),
    paddingValues: PaddingValues,
    bottomAppBarScrollBehavior: BottomAppBarScrollBehavior,
    searchType: String = "vehicle",
    onCancel: () -> Unit,
    onSearch: (String) -> Unit,
    onVehicleSelected: (Int) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SearchScreen(
        state = state,
        bottomAppBarScrollBehavior = bottomAppBarScrollBehavior,
        onAction = viewModel::onAction,
        paddingValues = paddingValues,
        onCancel = onCancel,
        onSearch = onSearch,
        onVehicleSelected = onVehicleSelected
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    state: SearchScreenState,
    bottomAppBarScrollBehavior: BottomAppBarScrollBehavior,
    onAction: (SearchScreenAction) -> Unit,
    paddingValues: PaddingValues,
    onCancel: () -> Unit,
    onSearch: (String) -> Unit,
    onVehicleSelected: (Int) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(
                    top = paddingValues.calculateTopPadding()
                )
        ) {
            SearchTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                text = state.searchQuery,
                onTextChange = {
                    onAction(SearchScreenAction.SetSearchQuery(it))
                },
                onSearch = {
                    onSearch(state.searchQuery)
                },
                onCancel = onCancel
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(bottomAppBarScrollBehavior.nestedScrollConnection),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = paddingValues.calculateBottomPadding(),
                    top = 8.dp
                )
            ) {
                if (state.searchQuery.isNotBlank()) {
                    item {
                        SearchQueryItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            query = state.searchQuery,
                            onClick = {
                                onSearch(state.searchQuery)
                            }
                        )
                    }
                }
                when (val searchResult = state.searchResult) {
                    is UiState.Loading -> {
                        items(5) {
                            SearchResultItemSkeleton(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                    is UiState.Success -> {
                        items(searchResult.data) { vehicle ->
                            SearchResultItem(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                title = "${vehicle.model} ${vehicle.year} ${vehicle.color}",
                                place = vehicle.place,
                                spz = vehicle.spz,
                                onClick = {
                                    onVehicleSelected(vehicle.id!!)
                                }
                            )
                        }
                    }
                    else -> {}
                }
            }
        }
        AnimatedVisibility(
            visible = state.searchResult is UiState.Success && state.searchResult.data.isEmpty() && state.searchQuery.isBlank(),
            enter = fadeIn(),
            exit = fadeOut(),
        ){
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                Image(
                    painter = painterResource(Images.Search),
                    contentDescription = searchString(),
                    modifier = Modifier.size(96.dp)
                )
                Text(
                    text = stringResource(Res.string.searchDescription),
                    style = MaterialTheme.typography.titleSmall,
                    fontStyle = FontStyle.Italic,
                )
            }
        }
    }
}
