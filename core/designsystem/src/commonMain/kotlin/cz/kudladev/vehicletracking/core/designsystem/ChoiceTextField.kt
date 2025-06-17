package cz.kudladev.vehicletracking.core.designsystem

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import cz.kudladev.vehicletracking.core.designsystem.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> ChoiceTextField(
    modifier: Modifier = Modifier,
    value: String,
    options: List<T>,
    onValueChange: (T) -> Unit,
    label: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
){
    val focusManager = LocalFocusManager.current
    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = onExpandedChange,
    ) {
        OutlinedTextField(
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .then(modifier),
            onValueChange = {},
            value = value,
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded,
                )
            },
            shape = MaterialTheme.shapes.extraLarge,
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                focusManager.clearFocus()
                onExpandedChange(false)
            },
            shape = MaterialTheme.shapes.extraLarge,
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option.toString(),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    },
                    onClick = {
                        onValueChange(option)
                        onExpandedChange(false)
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}

@Preview
@Composable
private fun ChoiceTextFieldPreview() {
    AppTheme {
        Surface() {
            var expanded by remember { mutableStateOf(false) }
            ChoiceTextField(
                value = "Option 1",
                options = listOf("Option 1", "Option 2", "Option 3"),
                onValueChange = {},
                label = "Choose an option",
                expanded = expanded,
                onExpandedChange = {
                    expanded = it
                }
            )
        }
    }
}