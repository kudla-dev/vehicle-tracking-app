package cz.kudladev.vehicletracking.core.ui.others

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cz.kudladev.vehicletracking.core.designsystem.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LoadingDialog(
    title: String,
    isLoading: Boolean,
){
    AnimatedVisibility(
        visible = isLoading,
        enter = fadeIn(tween(300)),
        exit = fadeOut(tween(300))
    ) {
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
            )
        ){
            Column(
                modifier = Modifier
                    .sizeIn(
                        minWidth = 300.dp,
                        maxWidth = 400.dp,
                    )
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
                Box(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .sizeIn(minWidth = 48.dp, minHeight = 48.dp, maxWidth = 64.dp, maxHeight = 64.dp),
                    contentAlignment = Alignment.Center
                ){
                    CircularProgressIndicator(
                        gapSize = 45.dp
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun LoadingDialogPreview() {
    AppTheme {
        LoadingDialog(
            title = "Loading data, please wait...",
            isLoading = true
        )
    }
}