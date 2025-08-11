package cz.kudladev.vehicletracking.core.designsystem

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cz.kudladev.vehicletracking.core.designsystem.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
private fun Button(
    modifier: Modifier = Modifier,
    text: String,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    onClick: () -> Unit,
    enabled: Boolean = true,
    borderStroke: BorderStroke? = null,
    loading: Boolean = false,
    buttonColors: ButtonColors = ButtonDefaults.buttonColors(),
){
    Button(
        modifier = modifier
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(13.dp),
                spotColor = Color.Black.copy(alpha = 0.3f)
            )
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
        shape = RoundedCornerShape(13.dp),
        colors = buttonColors,
        border = borderStroke
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
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(13.dp),
                spotColor = Color.Black.copy(alpha = 0.3f)
            ),
        text = text,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        onClick = onClick,
        enabled = enabled,
        loading = loading,
        buttonColors = ButtonDefaults.run {
            buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
            )
        }
    )
}

@Composable
fun SecondaryButton(
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
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(13.dp),
                spotColor = Color.Black.copy(alpha = 0.3f)
            ),
        text = text,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        onClick = onClick,
        enabled = enabled,
        loading = loading,
        buttonColors = ButtonDefaults.run {
            buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
                disabledContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                disabledContentColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.5f)
            )
        },
//        borderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.onSecondary)
    )
}

@Composable
@Preview
private fun PrimaryButtonPreview() {
    AppTheme {
        Surface {
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
}

@Composable
@Preview
private fun SecondaryButtonPreview() {
    AppTheme {
        Surface {
            SecondaryButton(
                text = "Secondary Button",
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
}