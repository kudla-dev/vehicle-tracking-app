package cz.kudladev.vehicletracking.feature.onboarding.register

import StackedSnackbarAnimation
import StackedSnackbarDuration
import StackedSnackbarHost
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import cz.kudladev.vehicletracking.core.designsystem.BackButton
import cz.kudladev.vehicletracking.core.designsystem.KeyboardClearFocus
import cz.kudladev.vehicletracking.core.designsystem.LargeTopAppBar
import cz.kudladev.vehicletracking.core.designsystem.OutlinedTextField
import cz.kudladev.vehicletracking.feature.onboarding.register.components.PasswordTextField
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import rememberStackedSnackbarHostState

@Serializable
data object Register

@Composable
fun RegisterScreenRoot(
    viewModel: RegisterScreenViewModel = koinViewModel(),
    onBack: () -> Unit,
    onRegisterConfirmed: () -> Unit
){
    val state = viewModel.state.collectAsStateWithLifecycle()

    RegisterScreen(
        state = state.value,
        onAction = viewModel::onAction,
        onBack = onBack,
        onRegisterConfirmed = onRegisterConfirmed
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegisterScreen(
    state: RegisterScreenState,
    onAction: (RegisterScreenAction) -> Unit,
    onBack: () -> Unit,
    onRegisterConfirmed: () -> Unit
){
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    val focusManager = LocalFocusManager.current

    val stackedSnackBarHostState = rememberStackedSnackbarHostState(
        maxStack = 1,
        animation = StackedSnackbarAnimation.Slide
    )

    LaunchedEffect(state.registrationProcess) {
        when (state.registrationProcess){
            is RegistrationProcess.Error -> {
                stackedSnackBarHostState.showErrorSnackbar(
                    title = "Registration failed",
                    description = state.registrationProcess.message.message,
                    duration = StackedSnackbarDuration.Short,

                )
            }
            RegistrationProcess.Idle -> {}
            RegistrationProcess.Loading -> {
                stackedSnackBarHostState.showInfoSnackbar(
                    title = "Registering",
                    description = "Please wait...",
                    duration = StackedSnackbarDuration.Short
                )
            }
            RegistrationProcess.Success -> {
                stackedSnackBarHostState.showSuccessSnackbar(
                    title = "Registration successful",
                    description = "You can now log in",
                )
                onRegisterConfirmed()
            }
        }
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "Register",
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
        snackbarHost = { StackedSnackbarHost(hostState = stackedSnackBarHostState)  },
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .imePadding()
    ) { innerPadding ->
        KeyboardClearFocus {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    modifier = Modifier.widthIn(500.dp, 750.dp).padding(horizontal = 8.dp),
                    value = state.firstName,
                    onValueChange = {
                        onAction(RegisterScreenAction.OnFirstNameChanged(it))
                    },
                    label = {
                        Text(
                            text = "First name"
                        )
                    },
                    info = "Ex. John",
                    maxLines = 1,
                    singleLine = true,
                    error = state.firstNameError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
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
                OutlinedTextField(
                    modifier = Modifier.widthIn(500.dp, 750.dp).padding(horizontal = 8.dp),
                    value = state.lastName,
                    onValueChange = {
                        onAction(RegisterScreenAction.OnLastNameChanged(it))
                    },
                    label = {
                        Text(
                            text = "Last name"
                        )
                    },
                    info = "Ex. Stones",
                    maxLines = 1,
                    singleLine = true,
                    error = state.lastNameError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
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
                OutlinedTextField(
                    modifier = Modifier.widthIn(500.dp, 750.dp).padding(horizontal = 8.dp),
                    value = state.email,
                    onValueChange = {
                        onAction(RegisterScreenAction.OnEmailChanged(it))
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
                        autoCorrectEnabled = false,
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
                OutlinedTextField(
                    modifier = Modifier.widthIn(500.dp, 750.dp).padding(horizontal = 8.dp),
                    value = state.phoneNumber,
                    onValueChange = {
                        onAction(RegisterScreenAction.OnPhoneNumberChanged(it))
                    },
                    label = {
                        Text(
                            text = "Phone number"
                        )
                    },
                    info = "Ex. +420123456789",
                    maxLines = 1,
                    singleLine = true,
                    error = state.phoneNumberError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next,
                        autoCorrectEnabled = false,
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(
                                focusDirection = FocusDirection.Down
                            )
                        }
                    )
                )
                PasswordTextField(
                    modifier = Modifier.widthIn(500.dp, 750.dp).padding(horizontal = 8.dp),
                    password = state.password,
                    onPasswordChange = {
                        if (!it.any { it.isWhitespace() }){
                            onAction(RegisterScreenAction.OnPasswordChanged(it))
                        }
                    },
                    error = state.passwordError,
                    focusManager = focusManager,
                    passwordVisible = state.visiblePassword,
                    onPasswordVisibleChange = {
                        onAction(RegisterScreenAction.OnPasswordVisibleChanged(it))
                    }
                )
                OutlinedTextField(
                    modifier = Modifier.widthIn(500.dp, 750.dp).padding(horizontal = 8.dp),
                    value = state.confirmPassword,
                    onValueChange = {
                        if (!it.any { it.isWhitespace() }){
                            onAction(RegisterScreenAction.OnConfirmPasswordChanged(it))
                        }
                    },
                    label = {
                        Text(
                            text = "Confirm password"
                        )
                    },
                    info = "Password must match",
                    maxLines = 1,
                    singleLine = true,
                    error = state.confirmPasswordError,
                    visualTransformation = if (state.visiblePassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                onAction(RegisterScreenAction.OnPasswordVisibleChanged(!state.visiblePassword))
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
                        imeAction = ImeAction.Done,
                        autoCorrectEnabled = false,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            if (state.registrationProcess !is RegistrationProcess.Loading){
                                onAction(RegisterScreenAction.OnRegister)
                            } else {
                                stackedSnackBarHostState.showInfoSnackbar(
                                    title = "Registering",
                                    description = "Please wait...",
                                    duration = StackedSnackbarDuration.Short
                                )
                            }
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
                            focusManager.clearFocus()
                            if (state.registrationProcess !is RegistrationProcess.Loading){
                                onAction(RegisterScreenAction.OnRegister)
                            } else {
                                stackedSnackBarHostState.showInfoSnackbar(
                                    title = "Registering",
                                    description = "Please wait...",
                                    duration = StackedSnackbarDuration.Short
                                )
                            }
                        }
                    ) {
                        Text(
                            text = "Register"
                        )
                    }
                }
            }
        }
    }
}