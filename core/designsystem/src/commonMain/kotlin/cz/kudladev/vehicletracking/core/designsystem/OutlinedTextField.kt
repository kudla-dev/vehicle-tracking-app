package cz.kudladev.vehicletracking.core.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import cz.kudladev.vehicletracking.core.designsystem.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun textFieldColors(): TextFieldColors =
TextFieldDefaults.colors().copy(
    focusedTextColor = MaterialTheme.colorScheme.onSecondary,
    unfocusedTextColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.7f),
    disabledTextColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.38f),
    errorTextColor = MaterialTheme.colorScheme.error,
    focusedContainerColor = MaterialTheme.colorScheme.secondary,
    unfocusedContainerColor = MaterialTheme.colorScheme.secondary,
    disabledContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f),
    errorContainerColor = Color.Unspecified,
    cursorColor = MaterialTheme.colorScheme.primary,
    errorCursorColor = MaterialTheme.colorScheme.error,
    textSelectionColors = TextSelectionColors(
        handleColor = MaterialTheme.colorScheme.primary,
        backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
    ),
    focusedIndicatorColor = MaterialTheme.colorScheme.secondary,
    unfocusedIndicatorColor = MaterialTheme.colorScheme.secondary,
    disabledIndicatorColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f),
    errorIndicatorColor = MaterialTheme.colorScheme.error,
    focusedLeadingIconColor = MaterialTheme.colorScheme.onSecondary,
    unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.7f),
    disabledLeadingIconColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.38f),
    errorLeadingIconColor = MaterialTheme.colorScheme.error,
    focusedTrailingIconColor = MaterialTheme.colorScheme.onSecondary,
    unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.7f),
    disabledTrailingIconColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.38f),
    errorTrailingIconColor = MaterialTheme.colorScheme.error,
    focusedLabelColor = MaterialTheme.colorScheme.onSecondary,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.7f),
    disabledLabelColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.38f),
    errorLabelColor = MaterialTheme.colorScheme.error,
    focusedPlaceholderColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.5f),
    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.5f),
    disabledPlaceholderColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.38f),
    errorPlaceholderColor = MaterialTheme.colorScheme.error,
    focusedSupportingTextColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.5f),
    unfocusedSupportingTextColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.5f),
    disabledSupportingTextColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.38f),
    errorSupportingTextColor = MaterialTheme.colorScheme.error,
    focusedPrefixColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.5f),
    unfocusedPrefixColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.5f),
    disabledPrefixColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.38f),
    errorPrefixColor = MaterialTheme.colorScheme.error,
    focusedSuffixColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.5f),
    unfocusedSuffixColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.5f),
    disabledSuffixColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.38f),
    errorSuffixColor = MaterialTheme.colorScheme.error,
)

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
    readOnly: Boolean = false,
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
        TextField(
            modifier = modifier,
            value = value,
            onValueChange = onValueChange,
            label = label,
            readOnly = readOnly,
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
            shape = RoundedCornerShape(13.dp),
            colors = textFieldColors()
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
                        color = MaterialTheme.colorScheme.onSurface,
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

