package cz.kudladev.vehicletracking.core.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import cz.kudladev.vehicletracking.core.designsystem.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun OutlinedTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable (() -> Unit)? = null,
    info: String? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    error: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int. MAX_VALUE,
    minLines: Int = 1,
){
    Column(
        modifier = modifier
    ) {
        OutlinedTextField(
            modifier = modifier,
            value = value,
            onValueChange = onValueChange,
            label = label,
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            isError = if (error != null) true else false,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            prefix = prefix,
            suffix = suffix,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
            shape = MaterialTheme.shapes.extraLarge,
        )
        when (error != null) {
            true -> {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = modifier.padding(start = 8.dp),
                    softWrap = true,
                    maxLines = 2,
                    minLines = 1
                )
            }
            false -> {
                info?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = modifier.padding(start = 16.dp),
                        softWrap = true
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun OutlinedTextFieldNonFilledPreview() {
    AppTheme {
        Box(
            modifier = Modifier.background(Color.White)
        ) {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Label") },
                info = "This is an info text",
                placeholder = { Text("Placeholder") },
                singleLine = true
            )
        }
    }
}

@Preview
@Composable
private fun OutlinedTextFieldFilledPreview() {
    AppTheme {
        Box(
            modifier = Modifier.background(Color.White)
        ) {
            OutlinedTextField(
                value = "Some filled text",
                onValueChange = {},
                label = { Text("Label") },
                info = "This is an info text",
                placeholder = { Text("Placeholder") },
                singleLine = true,
            )
        }
    }
}

@Preview
@Composable
private fun OutlinedTextFieldWithIconsPreview() {
    AppTheme {
        Box(
            modifier = Modifier.background(Color.White)
        ) {
            OutlinedTextField(
                value = "Text with icons",
                onValueChange = {},
                label = { Text("Label") },
                info = "This is an info text",
                placeholder = { Text("Placeholder") },
                leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null) },
                trailingIcon = { Icon(imageVector = Icons.Default.Check, contentDescription = null) },
                singleLine = true,
            )
        }
    }
}

@Preview
@Composable
private fun OutlinedTextFieldErrorPreview() {
    AppTheme {
        Box(
            modifier = Modifier.background(Color.White)
        ) {
            OutlinedTextField(
                value = "Error text",
                onValueChange = {},
                label = { Text("Label") },
                info = "This is an info text",
                placeholder = { Text("Placeholder") },
                error = "This is an error message",
                singleLine = true,
            )
        }
    }
}

