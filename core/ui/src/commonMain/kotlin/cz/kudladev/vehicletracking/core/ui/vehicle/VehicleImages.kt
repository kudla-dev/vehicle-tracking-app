package cz.kudladev.vehicletracking.core.ui.vehicle

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import cz.kudladev.vehicletracking.core.designsystem.theme.AppTheme
import cz.kudladev.vehicletracking.core.ui.util.toImageBitmap
import cz.kudladev.vehicletracking.model.Image
import cz.kudladev.vehicletracking.model.ImageWithBytes
import cz.kudladev.vehicletracking.model.ImageWithUrl
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun VehicleImages(
    modifier: Modifier = Modifier,
    images: List<Image>,
    onImageClick: (String) -> Unit,
    onImageRemove: ((Int) -> Unit)? = null,
    onImagesReordered: (List<Image>) -> Unit
) {
    var imageList by remember { mutableStateOf(images) }
    val pagerState = rememberPagerState(pageCount = { images.size })


    LaunchedEffect(images) {
        imageList = images
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        when (images.isEmpty()){
            true -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No images available",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            false -> {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f),
                    pageSpacing = 8.dp,
                    userScrollEnabled = true,
                    flingBehavior = PagerDefaults.flingBehavior(
                        state = pagerState,
                        snapAnimationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ),
//                    key = { index -> images.getOrNull(index) ?: index }
                ) { index ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(MaterialTheme.shapes.medium),
                        contentAlignment = Alignment.Center
                    ) {
                        when (val image = images.getOrNull(index)) {
                            is ImageWithBytes -> {
                                image.bytes?.toImageBitmap()?.let {
                                    Image(
                                        bitmap = it,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                            is ImageWithUrl -> {
                                AsyncImage(
                                    model = image.url,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                        onImageRemove?.let {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.TopEnd
                            ){
                                IconButton(
                                    colors = IconButtonDefaults.iconButtonColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceDim.copy(alpha = 0.5f),
                                        contentColor = MaterialTheme.colorScheme.onSurface
                                    ),
                                    onClick = {
                                        onImageRemove(index)
                                    },
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .size(24.dp)
                                ){
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove image",
                                    )
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(images.size) { index ->
                        val isSelected = pagerState.currentPage == index
                        val width = animateDpAsState(
                            targetValue = if (isSelected) 24.dp else 8.dp,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            label = "width"
                        )
                        val height = 8.dp

                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(width = width.value, height = height)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.outlineVariant
                                )
                                .animateContentSize()
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun VehicleImagesPreview() {
    val images = listOf(
        ImageWithUrl(url = "https://cdn.motopark.cz/images/0/bedc811d0cdcea9a/2/yamaha-tenere-700-2025-icon-blue.png?hash=488245943", position = 0),
        ImageWithUrl(url = "https://cdn.motopark.cz/images/0/bedc811d0cdcea9a/2/yamaha-tenere-700-2025-icon-blue.png?hash=488245943", position = 1),
        ImageWithUrl(url = "https://cdn.motopark.cz/images/0/bedc811d0cdcea9a/2/yamaha-tenere-700-2025-icon-blue.png?hash=488245943", position = 2),
    )

    AppTheme {
        Surface {
            VehicleImages(
                modifier = Modifier.height(300.dp),
                images = images,
                onImageClick = {},
                onImageRemove = null,
                onImagesReordered = {}
            )
        }
    }
}