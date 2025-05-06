package cz.kudladev.vehicletracking.auth.presentation.login

import StackedSnackbarHost
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cz.kudladev.vehicletracking.core.presentation.components.BackButton
import cz.kudladev.vehicletracking.core.presentation.components.KeyboardClearFocus
import cz.kudladev.vehicletracking.core.presentation.components.TextField
import cz.kudladev.vehicletracking.core.presentation.components.TopAppBar
import org.koin.compose.viewmodel.koinViewModel
import rememberStackedSnackbarHostState

@Composable
fun LoginScreenRoot(
    viewModel: LoginScreenViewModel = koinViewModel(),
    onBack: () -> Unit,
    onLoginConfirm: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LoginScreenScreen(
        state = state,
        onAction = viewModel::onAction,
        onBack = onBack,
        onLoginConfirm = onLoginConfirm
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreenScreen(
    state: LoginScreenState,
    onAction: (LoginScreenAction) -> Unit,
    onBack: () -> Unit,
    onLoginConfirm: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    val focusManager = LocalFocusManager.current

    val stackedSnackBarHostState = rememberStackedSnackbarHostState(
        maxStack = 1,
        animation = StackedSnackbarAnimation.Slide
    )

    LaunchedEffect(state.loginProgress) {
        when(state.loginProgress){
            is LoginProgress.Error -> {
                stackedSnackBarHostState.showErrorSnackbar(
                    title = "Login failed",
                    description = state.loginProgress.message.message,
                    duration = StackedSnackbarDuration.Short
                )
            }
            LoginProgress.LoggedIn -> {
                stackedSnackBarHostState.showErrorSnackbar(
                    title = "Logged in",
                    duration = StackedSnackbarDuration.Short
                )
                onLoginConfirm()
            }
            LoginProgress.Idle, LoginProgress.Loading,LoginProgress.Success  -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Login"
                    )
                },
                navigationIcon = {
                    BackButton(
                        onClick = onBack
                    )
                },
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier
            .imePadding()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = {
            StackedSnackbarHost(hostState = stackedSnackBarHostState)
        }
    ) { innerPadding ->
        KeyboardClearFocus {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                TextField(
                    modifier = Modifier.widthIn(500.dp, 550.dp).padding(horizontal = 8.dp),
                    value = state.email,
                    onValueChange = {
                        onAction(LoginScreenAction.OnEmailChanged(it))
                    },
                    label = {
                        Text(
                            text = "Email"
                        )
                    },
                    info = "Ex. john.stones@example.com",
                    maxLines = 1,
                    singleLine = true,
                    error = state.emailError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(
                                focusDirection = FocusDirection.Down
                            )
                        }
                    )
                )
                TextField(
                    modifier = Modifier.widthIn(500.dp, 750.dp).padding(horizontal = 8.dp),
                    value = state.password,
                    onValueChange = {
                        onAction(LoginScreenAction.OnPasswordChanged(it))
                    },
                    label = {
                        Text(
                            text = "Password"
                        )
                    },
                    maxLines = 1,
                    singleLine = true,
                    error = state.passwordError,
                    visualTransformation = if (state.visiblePassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                onAction(LoginScreenAction.OnPasswordVisibleChanged(!state.visiblePassword))
                            }
                        ){
                            Icon(
                                imageVector = if (state.visiblePassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "password",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            onAction(LoginScreenAction.OnLogin)
                        }
                    ),
                )
                Row(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            onAction(LoginScreenAction.OnLogin)
                        }
                    ) {
                        Text(
                            text = "Login"
                        )
                    }
                }
            }
        }
    }
}
