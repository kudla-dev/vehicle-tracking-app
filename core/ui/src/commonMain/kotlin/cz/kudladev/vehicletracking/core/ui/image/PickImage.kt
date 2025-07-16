package cz.kudladev.vehicletracking.core.ui.image

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import cz.kudladev.vehicletracking.core.designsystem.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun PickImage(
    onClick: () -> Unit,
){
}


@Preview
@Composable
fun PickImagePreview() {
    AppTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            PickImage(
                onClick = {}
            )
        }
    }
}