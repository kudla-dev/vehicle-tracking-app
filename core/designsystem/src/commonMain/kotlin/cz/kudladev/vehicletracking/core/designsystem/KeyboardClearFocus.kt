package cz.kudladev.vehicletracking.core.designsystem

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager

@Composable
fun KeyboardClearFocus(
    content: @Composable () -> Unit
) {
    val focusManager = LocalFocusManager.current
    Box(modifier = Modifier
        .fillMaxSize()
        .clickable(
            onClick = { focusManager.clearFocus() },
            indication = null,
            interactionSource = null
        )
    ) {
        content()
    }
}