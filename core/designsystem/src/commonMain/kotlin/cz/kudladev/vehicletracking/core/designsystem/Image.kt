package cz.kudladev.vehicletracking.core.designsystem

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.HideImage
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.InternalLandscapistApi
import com.skydoves.landscapist.coil3.CoilImage

@OptIn(InternalLandscapistApi::class)
@Composable
fun Image(
    modifier: Modifier = Modifier,
    imageUrl: String,
    contentScale: ContentScale = ContentScale.Crop,
){
    CoilImage(
        modifier = modifier,
        imageModel = { imageUrl },
        loading = {
            Box(modifier = Modifier.matchParentSize()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        },
        failure = {
            Box(modifier = Modifier.matchParentSize(), contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.TwoTone.HideImage,
                    contentDescription = "Image not available",
                )
            }
        },
        imageOptions = ImageOptions(
            contentScale = contentScale
        ),

    )
}