package cz.kudladev.vehicletracking.core.designsystem

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cz.kudladev.vehicletracking.core.designsystem.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter

@Composable
fun PrimaryButton(
    modifier: Modifier = Modifier,
    text: String,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    onClick: () -> Unit,
    enabled: Boolean = true,
    loading: Boolean = false,
){
    Button(
        modifier = modifier
            .widthIn(240.dp, 500.dp),
        onClick = {
            if (!loading) {
                onClick()
            }
        },
        contentPadding = PaddingValues(
            horizontal = 24.dp,
            vertical = 16.dp
        ),
        shape = RoundedCornerShape(13.dp)
    ){
        Crossfade(targetState = loading) { loading ->
            when (loading) {
                true -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp)
                    )
                }
                false -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                    leadingIcon?.invoke()
                    Text(
                        text = text,
                        fontWeight = FontWeight.Bold,
                    )
                    trailingIcon?.invoke()
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun PrimaryButtonPreview() {
    AppTheme {
        PrimaryButton(
            text = "Primary Button",
            loading = false,
            onClick = {},
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Menu,
                    contentDescription = "Leading Icon"
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Menu,
                    contentDescription = "Trailing Icon"
                )
            }
        )
    }
}