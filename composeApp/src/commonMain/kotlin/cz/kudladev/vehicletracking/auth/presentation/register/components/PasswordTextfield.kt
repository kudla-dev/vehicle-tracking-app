package cz.kudladev.vehicletracking.auth.presentation.register.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cz.kudladev.vehicletracking.core.presentation.components.TextField

@Composable
fun PasswordTextField(
    modifier: Modifier = Modifier,
    password: String,
    onPasswordChange: (String) -> Unit,
    error: String? = null,
    focusManager: FocusManager,
    passwordVisible: Boolean = false,
    onPasswordVisibleChange: (Boolean) -> Unit = {}
){

    val showError = error != null

    val tooShort = password.length < 8
    val hasUpperCase = password.any { it.isUpperCase() }
    val hasSpecialChar = password.any { it.isLetterOrDigit().not() }
    val hasNumber = password.any { it.isDigit() }

    val strongPercentage = (if (tooShort) 0 else 1) +
            (if (hasUpperCase) 1 else 0) +
            (if (hasSpecialChar) 1 else 0) +
            (if (hasNumber) 1 else 0)

    val animated by animateFloatAsState(
        targetValue = strongPercentage.toFloat() / 4f,
        label = "passwordStrongPercentage"
    )


    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        TextField(
            modifier = modifier,
            value = password,
            onValueChange = onPasswordChange,
            label = {
                Text(
                    text = "Password"
                )
            },
            maxLines = 1,
            singleLine = true,
            error = error,
            showError = false,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next,
                autoCorrectEnabled = false,
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(
                        focusDirection = FocusDirection.Down
                    )
                }
            ),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(
                    onClick = {
                        onPasswordVisibleChange(!passwordVisible)
                    }
                ){
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "password",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
        Box(
            modifier = modifier
                .padding(start = 16.dp, end = 16.dp, top = 8.dp)
                .height(6.dp)
                .clip(MaterialTheme.shapes.extraLarge)
                .background(MaterialTheme.colorScheme.outlineVariant),
            contentAlignment = Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animated)
                    .height(4.dp)
                    .clip(MaterialTheme.shapes.extraLarge)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
        Column(
            modifier = Modifier
                .padding(
                    top = 8.dp,
                    start = 24.dp,
                    end = 24.dp
                ),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            PasswordRule(
                rule = "At least 8 characters",
                valid = !tooShort,
                showError = showError
            )
            PasswordRule(
                rule = "At least 1 uppercase letter",
                valid = hasUpperCase,
                showError = showError
            )
            PasswordRule(
                rule = "At least 1 special character",
                valid = hasSpecialChar,
                showError = showError
            )
            PasswordRule(
                rule = "At least 1 number",
                valid = hasNumber,
                showError = showError
            )
        }
    }
}

@Composable
fun PasswordRule(
    rule: String,
    valid: Boolean,
    showError: Boolean = false
){
    val rotate by animateFloatAsState(
        if (valid) 0f else 360f,
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Crossfade(
            targetState = valid
        ) {
            when (it) {
                true -> {
                    Icon(
                        modifier = Modifier
                            .height(16.dp)
                            .rotate(rotate),
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                false -> {
                    Icon(
                        modifier = Modifier
                            .height(16.dp)
                            .rotate(-rotate),
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = if (showError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        Text(
            text = rule,
            style = MaterialTheme.typography.bodySmall,
            color = if (showError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}