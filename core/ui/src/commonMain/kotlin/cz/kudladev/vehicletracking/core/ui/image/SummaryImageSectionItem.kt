package cz.kudladev.vehicletracking.core.ui.image

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil3.CoilImage
import cz.kudladev.vehicletracking.core.designsystem.Badge
import cz.kudladev.vehicletracking.core.designsystem.PrimaryButton
import cz.kudladev.vehicletracking.core.designsystem.theme.AppTheme
import cz.kudladev.vehicletracking.core.ui.util.toImageBitmap
import cz.kudladev.vehicletracking.model.ErrorMessage
import cz.kudladev.vehicletracking.model.ImageUploadState
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import vehicletracking.core.ui.generated.resources.Res
import vehicletracking.core.ui.generated.resources.after
import vehicletracking.core.ui.generated.resources.before

@Composable
fun SummaryImageSectionItem(
    beforeImage: ImageUploadState? = null,
    onRetryBeforeImage: (() -> Unit)? = null,
    afterImage: ImageUploadState? = null,
    onRetryAfterImage: (() -> Unit)? = null,
    title: String,
){
    Column {
        // Title section
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))

        val imageModifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(MaterialTheme.shapes.medium)

        // Main image
        if (beforeImage != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 4.dp,
                        shape = MaterialTheme.shapes.medium,
                        clip = true
                    )
            ) {
                // Image content first (as background)
                Crossfade(
                    animationSpec = tween(durationMillis = 300),
                    targetState = beforeImage
                ) { beforeImageState ->
                    when (beforeImageState) {
                        is ImageUploadState.Queued -> {
                            WaitingForUpload(imageModifier)
                        }
                        is ImageUploadState.Uploading -> {
                            Uploading(imageModifier, beforeImageState)
                        }
                        is ImageUploadState.Completed -> {
                            Completed(beforeImageState, imageModifier)
                        }
                        is ImageUploadState.Canceled, is ImageUploadState.Failed -> {
                            Error(imageModifier, onRetryBeforeImage)
                        }
                    }
                }

                // Text on top of everything
                if (afterImage != null) {
                    Badge(
                        modifier = Modifier.padding(8.dp),
                        text = stringResource(Res.string.before),
                        textSize = 14.sp,
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // "After" image section
        if (afterImage != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 4.dp,
                        shape = MaterialTheme.shapes.medium,
                        clip = true
                    )
            ) {
                // Image content first (as background)
                when (afterImage) {
                    is ImageUploadState.Queued -> {
                        WaitingForUpload(imageModifier)
                    }
                    is ImageUploadState.Uploading -> {
                        Uploading(imageModifier, afterImage)
                    }
                    is ImageUploadState.Completed -> {
                        Completed(
                            imageState = afterImage,
                            imageModifier = imageModifier
                        )
                    }
                    is ImageUploadState.Canceled, is ImageUploadState.Failed -> {
                        Error(imageModifier, onRetryAfterImage)
                    }
                }

                Badge(
                    modifier = Modifier.padding(8.dp),
                    text = stringResource(Res.string.after),
                    textSize = 14.sp,
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun Error(imageModifier: Modifier, onRetryBeforeImage: (() -> Unit)?) {
    Column(
        modifier = imageModifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        PrimaryButton(
            text = "Upload again",
            onClick = onRetryBeforeImage ?: {},
        )
    }
}

@Composable
private fun Completed(
    imageState: ImageUploadState.Completed,
    imageModifier: Modifier
) {
    CoilImage(
        imageModel = { imageState.imageURL.url },
        modifier = imageModifier
            .background(MaterialTheme.colorScheme.surface),
        imageOptions = ImageOptions(
            contentScale = ContentScale.Crop,
        )
    )
}

@Composable
private fun Uploading(
    imageModifier: Modifier,
    imageState: ImageUploadState.Uploading
) {
    Box(
        modifier = imageModifier,
        contentAlignment = Alignment.Center
    ) {
        val bitmap = imageState.imageUri?.toImageBitmap()
        bitmap?.let {
            Image(
                bitmap = bitmap,
                contentDescription = null,
                modifier = imageModifier,
                contentScale = ContentScale.Crop,
            )
        }
        Column(
            modifier = imageModifier
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.3f))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LinearProgressIndicator(
                progress = { imageState.progress },
                modifier = Modifier.fillMaxWidth(0.6f),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            )
            Text(
                text = imageState.progress.toFormatedPercentage(),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontStyle = FontStyle.Italic
            )
        }
    }
}

@Composable
private fun WaitingForUpload(imageModifier: Modifier) {
    Column(
        modifier = imageModifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Waiting for upload",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontStyle = FontStyle.Italic
        )
    }
}

private fun Float.toFormatedPercentage(): String {
    return String.format("%.0f%%", this * 100)
}


@Composable
@Preview
fun SummaryImageSectionItemPreview() {
    AppTheme {
        SummaryImageSectionItem(
            beforeImage = ImageUploadState.Failed(
                errorMessage = ErrorMessage(
                    error = "",
                    message = "",
                    path = "",
                    status = 400,
                    timestamp = ""
                )
            ),
            onRetryBeforeImage = {},
            afterImage = ImageUploadState.Queued,
            onRetryAfterImage = {},
            title = "FrontView"
        )
    }
}