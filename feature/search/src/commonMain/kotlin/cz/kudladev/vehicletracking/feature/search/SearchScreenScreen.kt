package cz.kudladev.vehicletracking.feature.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.kudladev.vehicletracking.core.ui.search.SearchTextField
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SearchScreenRoot(
    viewModel: SearchScreenViewModel = koinViewModel(),
    paddingValues: PaddingValues,
    searchType: String = "vehicle",
    onCancel: () -> Unit,
    onSearch: (String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SearchScreenScreen(
        state = state,
        onAction = viewModel::onAction,
        paddingValues = paddingValues,
        onCancel = onCancel,
        onSearch = onSearch
    )
}

@Composable
fun SearchScreenScreen(
    state: SearchScreenState,
    onAction: (SearchScreenAction) -> Unit,
    paddingValues: PaddingValues,
    onCancel: () -> Unit,
    onSearch: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = paddingValues.calculateTopPadding()
            )
    ) {
        SearchTextField(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            text = state.searchQuery,
            onTextChange = {
                onAction(SearchScreenAction.SetSearchQuery(it))
            },
            onSearch = {
                onSearch(state.searchQuery)
            },
            onCancel = onCancel
        )
    }
}
