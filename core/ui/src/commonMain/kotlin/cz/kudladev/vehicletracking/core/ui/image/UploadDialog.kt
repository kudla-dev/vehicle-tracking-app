package cz.kudladev.vehicletracking.core.ui.image

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import cz.kudladev.vehicletracking.core.designsystem.theme.AppTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import vehicletracking.core.ui.generated.resources.Res
import vehicletracking.core.ui.generated.resources.uploadProgress
import vehicletracking.core.ui.generated.resources.uploadedOfImages
import vehicletracking.core.ui.generated.resources.uploadingImages

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadDialog(
    images: Int,
    uploadedImages: Float,
) {

    val animatedProgress = animateFloatAsState(
        targetValue = if (images == 0) {
            0f
        } else {
            uploadedImages / images
        },
        label = stringResource(Res.string.uploadProgress),
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
    )

    AlertDialog(
        icon = {},
        title = {
            Text(
                text = stringResource(Res.string.uploadingImages),
                fontStyle = FontStyle.Italic
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LinearProgressIndicator(
                    progress = { animatedProgress.value },
                    modifier = Modifier.fillMaxWidth(0.8f),
                    color = ProgressIndicatorDefaults.linearColor,
                    trackColor = ProgressIndicatorDefaults.linearTrackColor,
                    strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                    drawStopIndicator = {}
                )
                Text(
                    text = stringResource(Res.string.uploadedOfImages, uploadedImages.toInt(),images),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        onDismissRequest = {},
        confirmButton = {},
        dismissButton = {},
        containerColor = MaterialTheme.colorScheme.background,
        textContentColor = MaterialTheme.colorScheme.onBackground,
    )

}

@Preview
@Composable
private fun UploadDialogPreview() {
    AppTheme {
        UploadDialog(
            images = 10,
            uploadedImages = 5f
        )
    }
}