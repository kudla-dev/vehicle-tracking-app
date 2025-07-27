package cz.kudladev.vehicletracking.core.ui.image

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil3.CoilImage
import cz.kudladev.vehicletracking.core.ui.util.toImageBitmap
import cz.kudladev.vehicletracking.model.Image
import cz.kudladev.vehicletracking.model.ImageWithBytes
import cz.kudladev.vehicletracking.model.ImageWithUrl

@Composable
fun SummaryImage(
    image: Image? = null,
    nextImage: Image? = null,
    title: String? = null,
){
    Column {
        title?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        val imageModifier = Modifier
            .fillMaxWidth(0.9f)
            .aspectRatio(16f/9f)
            .clip(MaterialTheme.shapes.medium)
        nextImage?.let {
            Text(
                text = "After",
                style = MaterialTheme.typography.titleMedium,
            )
            when (it) {
                is ImageWithUrl -> {
                    CoilImage(
                        imageModel = {
                            it.url
                        },
                        modifier = imageModifier,
                        imageOptions = ImageOptions(
                            contentScale = ContentScale.Crop,
                        )
                    )
                }
                is ImageWithBytes -> {
                    Image(
                        bitmap = it.bytes?.toImageBitmap() ?: return@let,
                        contentDescription = null,
                        modifier = imageModifier,
                        contentScale = ContentScale.Crop,
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        nextImage?.let {
            Text(
                text = "Before",
                style = MaterialTheme.typography.titleMedium,
            )
        }
        when (image) {
            is ImageWithUrl -> {
                CoilImage(
                    imageModel = {
                        image.url
                    },
                    modifier = imageModifier,
                    imageOptions = ImageOptions(
                        contentScale = ContentScale.Crop,
                    )
                )
            }
            is ImageWithBytes -> {
                Image(
                    bitmap = image.bytes?.toImageBitmap() ?: return@Column,
                    contentDescription = null,
                    modifier = imageModifier,
                    contentScale = ContentScale.Crop,
                )
            }
        }


        Spacer(modifier = Modifier.height(16.dp))
    }
}