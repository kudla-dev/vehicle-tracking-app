package cz.kudladev.vehicletracking.feature.favourite


import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.BottomAppBarScrollBehavior
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
import org.jetbrains.compose.resources.stringResource
import vehicletracking.feature.favourite.generated.resources.Res
import vehicletracking.feature.favourite.generated.resources.favouriteTitle
@Serializable
data object Favourites

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouritesScreenRoot(
    paddingValues: PaddingValues,
    bottomAppBarScrollBehavior: BottomAppBarScrollBehavior
) {

    FavouritesScreen(
        paddingValues = paddingValues,
        bottomAppBarScrollBehavior = bottomAppBarScrollBehavior
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavouritesScreen(
    paddingValues: PaddingValues,
    bottomAppBarScrollBehavior: BottomAppBarScrollBehavior
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
            .nestedScroll(bottomAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        stringResource(Res.string.favouriteTitle),
                        fontStyle = FontStyle.Italic
                    )
                },
                scrollBehavior = topAppBarScrollBehavior,
            )
        },
    ) {

    }

}