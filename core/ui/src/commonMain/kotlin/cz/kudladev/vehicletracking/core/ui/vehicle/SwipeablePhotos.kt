package cz.kudladev.vehicletracking.core.ui.vehicle

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import cz.kudladev.vehicletracking.model.Image
import cz.kudladev.vehicletracking.model.ImageWithBytes
import cz.kudladev.vehicletracking.model.ImageWithUrl
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SwipeablePhotos(
    modifier: Modifier = Modifier,
    images: List<Image> = emptyList(),
) {
    val pagerState = rememberPagerState(pageCount = { images.size })
    var showIndicator by remember { mutableStateOf(false) }
    var firstLaunch by remember { mutableStateOf(true) }

    LaunchedEffect(pagerState.currentPage) {
        if (firstLaunch) {
            firstLaunch = false
        } else {
            showIndicator = true
            delay(1500)
            showIndicator = false
        }
    }

    Box(
        modifier = modifier,
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = modifier,
            pageSpacing = 0.dp,
            userScrollEnabled = true,
            flingBehavior = PagerDefaults.flingBehavior(
                state = pagerState,
                snapAnimationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
            key = { index -> index }
        ) { index ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = when (val image = images.getOrNull(index)) {
                        is ImageWithUrl -> image.url
                        is ImageWithBytes -> image.bytes
                        else -> null
                    },
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            AnimatedVisibility(
                visible = showIndicator,
                exit = fadeOut()
            ) {
                Text(
                    "${pagerState.currentPage + 1} / ${images.size}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White,
                        shadow = Shadow(
                            color = Color.Black,
                            offset = Offset(0f, 1f),
                            blurRadius = 4f
                        )
                    ),

                )
            }
        }
    }
}