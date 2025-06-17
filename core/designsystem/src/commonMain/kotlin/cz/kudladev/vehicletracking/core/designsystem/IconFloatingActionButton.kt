package cz.kudladev.vehicletracking.core.designsystem

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import cz.kudladev.vehicletracking.core.designsystem.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun IconFloatingActionButton(
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    FloatingActionButton(
        onClick = onClick,
        content = icon,
    )
}

@Preview
@Composable
private fun IconFloatingActionButtonPreview() {
    AppTheme {
        IconFloatingActionButton(
            icon = {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Icon"
                )
            },
            onClick = { /* Handle click */ }
        )
    }
}
