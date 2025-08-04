package cz.kudladev.vehicletracking.feature.favourite

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
import androidx.compose.ui.text.font.FontStyle
import cz.kudladev.vehicletracking.core.designsystem.LargeTopAppBar
import kotlinx.serialization.Serializable

@Serializable
data object Favourites

@Composable
fun FavouritesScreenRoot(
    paddingValues: PaddingValues,
) {

    FavouritesScreen(
        paddingValues = paddingValues,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavouritesScreen(
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
                    Text(
                        "Your Favourites",
                        fontStyle = FontStyle.Italic
                    )
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) {

    }

}