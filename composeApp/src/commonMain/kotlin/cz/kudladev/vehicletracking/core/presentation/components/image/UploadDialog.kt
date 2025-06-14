package cz.kudladev.vehicletracking.core.presentation.components.image

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
import androidx.compose.ui.unit.dp

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
        label = "Upload progress",
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
    )

    AlertDialog(
        icon = {
            Icon(
                imageVector = Icons.Default.Cloud,
                contentDescription = "Info"
            )
        },
        title = {
            Text(
                text = "Uploading images",
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
                )
                Text(
                    text = "Uploaded ${uploadedImages.toInt()}/$images images",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        onDismissRequest = {},
        confirmButton = {},
        dismissButton = {},
    )

}