package cz.kudladev.vehicletracking.core.ui.image

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil3.CoilImage
import cz.kudladev.vehicletracking.core.ui.util.toImageBitmap
import cz.kudladev.vehicletracking.model.Image
import cz.kudladev.vehicletracking.model.ImageWithBytes
import cz.kudladev.vehicletracking.model.ImageWithUrl
import org.jetbrains.compose.resources.stringResource
import vehicletracking.core.ui.generated.resources.Res
import vehicletracking.core.ui.generated.resources.after
import vehicletracking.core.ui.generated.resources.before

@Composable
fun SummaryImage(
    image: Image? = null,
    nextImage: Image? = null,
    title: String? = null,
){
    Column {
        // Title section
        if (title != null) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        val imageModifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(MaterialTheme.shapes.medium)

        // Main image
        if (image != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                // Image content first (as background)
                when (image) {
                    is ImageWithUrl -> {
                        CoilImage(
                            imageModel = { image.url },
                            modifier = imageModifier,
                            imageOptions = ImageOptions(
                                contentScale = ContentScale.Crop,
                            )
                        )
                    }

                    is ImageWithBytes -> {
                        val bitmap = image.bytes?.toImageBitmap()
                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap,
                                contentDescription = null,
                                modifier = imageModifier,
                                contentScale = ContentScale.Crop,
                            )
                        }
                    }
                }

                // Gradient overlay (as foreground)
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(MaterialTheme.shapes.medium)
                        .background(
                            brush = Brush.radialGradient(
                                0.1f to Color.Black.copy(alpha = 0.5f),
                                0.9f to Color.Transparent,
                                center = Offset.Zero,
                                radius = 500f,
                                tileMode = TileMode.Decal,
                            )
                        )
                )

                // Text on top of everything
                if (nextImage != null) {
                    Text(
                        text = stringResource(Res.string.before),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(Alignment.TopStart).padding(8.dp),
                        color = Color.White,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // "After" image section
        if (nextImage != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                // Image content first (as background)
                when (nextImage) {
                    is ImageWithUrl -> {
                        CoilImage(
                            imageModel = { nextImage.url },
                            modifier = imageModifier,
                            imageOptions = ImageOptions(
                                contentScale = ContentScale.Crop,
                            )
                        )
                    }

                    is ImageWithBytes -> {
                        val bitmap = nextImage.bytes?.toImageBitmap()
                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap,
                                contentDescription = null,
                                modifier = imageModifier,
                                contentScale = ContentScale.Crop,
                            )
                        }
                    }
                }

                // Gradient overlay (as foreground)
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(MaterialTheme.shapes.medium)
                        .background(
                            brush = Brush.radialGradient(
                                0.1f to Color.Black.copy(alpha = 0.5f),
                                0.9f to Color.Transparent,
                                center = Offset.Zero,
                                radius = 500f,
                                tileMode = TileMode.Decal,
                            )
                        )
                )

                // Text on top of everything
                Text(
                    text = stringResource(Res.string.after),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.TopStart).padding(8.dp),
                    color = Color.White,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}