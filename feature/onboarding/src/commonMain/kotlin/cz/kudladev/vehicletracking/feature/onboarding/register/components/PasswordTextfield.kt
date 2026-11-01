package cz.kudladev.vehicletracking.feature.onboarding.register.components

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cz.kudladev.vehicletracking.core.designsystem.OutlinedTextField

@Composable
fun PasswordTextField(
    modifier: Modifier = Modifier,
    password: String,
    onPasswordChange: (String) -> Unit,
    error: String? = null,
    focusManager: FocusManager,
    passwordVisible: Boolean = false,
    onPasswordVisibleChange: (Boolean) -> Unit = {},
    testTag: String? = null,
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
        OutlinedTextField(
            modifier = modifier.then(if (testTag != null) Modifier.testTag(testTag) else Modifier),
            testTag = testTag,
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
                    modifier = Modifier.testTag("auth:register_password_toggle"),
                    onClick = {
                        onPasswordVisibleChange(!passwordVisible)
                    }
                ){
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Password",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, start = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(6.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
            ){
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animated)
                        .height(6.dp)
                        .clip(MaterialTheme.shapes.extraSmall)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
            Text(
                text = "${(animated * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}
