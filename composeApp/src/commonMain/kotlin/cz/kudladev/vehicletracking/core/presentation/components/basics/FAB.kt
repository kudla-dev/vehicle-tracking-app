package cz.kudladev.vehicletracking.core.presentation.components.basics

import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable

@Composable
fun FAB(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        content = content
    )
}