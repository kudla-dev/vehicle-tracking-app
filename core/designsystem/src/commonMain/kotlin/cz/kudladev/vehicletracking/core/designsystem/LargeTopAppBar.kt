package cz.kudladev.vehicletracking.core.designsystem

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cz.kudladev.vehicletracking.core.designsystem.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LargeTopAppBar(
    title: @Composable () -> Unit,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
    containerColor: Color = MaterialTheme.colorScheme.background,
    scrolledContainerColor: Color = MaterialTheme.colorScheme.background,
){
    LargeTopAppBar(
        modifier = Modifier
            .drawBehind {
                // Draw shadow only when the app bar is scrolled/collapsed
                if (scrollBehavior != null && scrollBehavior.state.collapsedFraction > 0f) {
                    // Draw a blurred drop shadow effect
                    val shadowColor = Color.Black
                    val shadowHeight = 8.dp.toPx()
                    val transparentColor = shadowColor.copy(alpha = 0f)

                    // Make shadow opacity proportional to scroll fraction
                    val shadowAlpha = 0.1f * scrollBehavior.state.collapsedFraction

                    // Create a vertical gradient for the shadow that fades out
                    val colors = listOf(
                        shadowColor.copy(alpha = shadowAlpha),
                        transparentColor
                    )

                    // Draw the shadow rectangle below the app bar
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = colors,
                            startY = size.height,
                            endY = size.height + shadowHeight
                        ),
                        topLeft = Offset(0f, size.height),
                        size = androidx.compose.ui.geometry.Size(size.width, shadowHeight)
                    )
                }
            },
        title = title,
        navigationIcon = navigationIcon,
        actions = {
            actions()
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor,
            scrolledContainerColor = scrolledContainerColor,
        ),
        scrollBehavior = scrollBehavior
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun LargeTopAppBarPreview() {
    AppTheme {
        LargeTopAppBar(
            title = {
                Text("Large Top App Bar")
            },
            navigationIcon = {
                BackButton(onClick = {})
            },
            actions = {
                IconButton(
                    onClick = {

                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings Icon",
                    )
                }
            }
        )
    }
}
