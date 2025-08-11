package cz.kudladev.vehicletracking.feature.menu.manage_vehicles

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import cz.kudladev.vehicletracking.core.designsystem.BackButton
import cz.kudladev.vehicletracking.core.designsystem.ChoiceTextField
import cz.kudladev.vehicletracking.core.designsystem.MediumTopAppBar
import cz.kudladev.vehicletracking.core.designsystem.OutlinedTextField
import cz.kudladev.vehicletracking.core.designsystem.PrimaryButton
import cz.kudladev.vehicletracking.core.designsystem.SecondaryButton
import cz.kudladev.vehicletracking.core.designsystem.theme.AppTheme
import cz.kudladev.vehicletracking.core.ui.image.UploadDialog
import cz.kudladev.vehicletracking.core.ui.vehicle.VehicleImages
import cz.kudladev.vehicletracking.model.Brand
import cz.kudladev.vehicletracking.model.ImageUploadState
import cz.kudladev.vehicletracking.model.ImageUploadStatus
import kotlinx.serialization.Serializable
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object ManageVehiclesAddEdit

@Composable
fun AddEditVehicleRoot(
    viewModel: AddEditVehicleViewModel = koinViewModel(),
    paddingValues: PaddingValues,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val uploadStatus by viewModel.uploadStatus.collectAsStateWithLifecycle()

    LaunchedEffect(uploadStatus, state.resultState) {
        println("Upload status changed: $uploadStatus and result state: ${state.resultState}")
        if (uploadStatus.isNotEmpty() && uploadStatus.size == state.images.size && uploadStatus.all { status -> status.state == ImageUploadState.COMPLETED } && state.resultState !is AddEditResultState.Error) {
            viewModel.onAction(AddEditVehicleAction.ClearRecentUploads)
            onBack()
        }
    }
    AddEditVehicleScreen(
        state = state,
        uploadStatus = uploadStatus,
        onAction = viewModel::onAction,
        paddingValues = paddingValues,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun AddEditVehicleScreen(
    state: AddEditVehicleState,
    uploadStatus: List<ImageUploadStatus>,
    onAction: (AddEditVehicleAction) -> Unit,
    paddingValues: PaddingValues,
    onBack: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val focusManager = LocalFocusManager.current

    val coroutinesScope = rememberCoroutineScope()

    val multipleImagePicker = rememberImagePickerLauncher(
        selectionMode = SelectionMode.Multiple(maxSelection = 5),
        scope = coroutinesScope,
        onResult = { byteArrays ->
            print("$byteArrays")
            byteArrays.forEach { byteArray ->
                onAction(AddEditVehicleAction.OnNewImageAdd(byteArray))
            }
        }
    )

    LaunchedEffect(state.images){
        println("Images changed: ${state.images}")
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        "Create new vehicle",
                        fontStyle = FontStyle.Italic,
                    )
                },
                navigationIcon = {
                    BackButton(onBack)
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        val combinedPadding = PaddingValues(
            start = innerPadding.calculateStartPadding(LocalLayoutDirection.current) + 16.dp,
            top = innerPadding.calculateTopPadding() + 16.dp,
            end = innerPadding.calculateEndPadding(LocalLayoutDirection.current) + 16.dp,
            bottom = paddingValues.calculateBottomPadding() + 16.dp,
        )
        LazyColumn(
            contentPadding = combinedPadding,
        ) {
            item {
                VehicleImages(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 12f),
                    images = state.images,
                    onImageClick = { image ->
                        // Handle image click
                    },
                    onImageRemove = { index ->
                        onAction(AddEditVehicleAction.OnImageRemove(index))
                    },
                    onImagesReordered = { images ->
                        onAction(AddEditVehicleAction.OnImagesReordered(images))
                    }
                )
                SecondaryButton(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = "Manage images",
                    onClick = {
                        onAction(AddEditVehicleAction.ToggleImageDialog)
                    }
                )
                state.imagesError?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(start = 8.dp),
                        softWrap = true,
                        maxLines = 2,
                        minLines = 1
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                ChoiceTextField<Brand>(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    value = state.brand?.name ?: "",
                    onValueChange = {
                        onAction(AddEditVehicleAction.OnBrandChange(it))
                    },
                    label = "Brand",
                    expanded = state.selectingBrand,
                    onExpandedChange = {
                        onAction(AddEditVehicleAction.ToggleBrandDialog)
                    },
                    options = state.brands
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    value = state.fullName,
                    onValueChange = {
                        onAction(AddEditVehicleAction.OnFullNameChange(it))
                    },
                    error = state.fullNameError,
                    label = {
                        Text("Full name")
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        autoCorrectEnabled = false,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(focusDirection = FocusDirection.Next)
                        }
                    )
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    value = state.model,
                    onValueChange = {
                        onAction(AddEditVehicleAction.OnModelChange(it))
                    },
                    error = state.modelError,
                    label = {
                        Text("Model")
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        autoCorrectEnabled = false,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(focusDirection = FocusDirection.Next)
                        }
                    )
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    value = state.year,
                    onValueChange = {
                        onAction(AddEditVehicleAction.OnYearChange(it))
                    },
                    error = state.yearError,
                    label = {
                        Text("Year")
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        autoCorrectEnabled = false,
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Number
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(focusDirection = FocusDirection.Next)
                        }
                    )
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    value = state.color,
                    onValueChange = {
                        onAction(AddEditVehicleAction.OnColorChange(it))
                    },
                    error = state.colorError,
                    label = {
                        Text("Color")
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        autoCorrectEnabled = false,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(focusDirection = FocusDirection.Next)
                        }
                    )
                )
                ChoiceTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    value = state.driverLicense,
                    onValueChange = {
                        onAction(AddEditVehicleAction.OnDriverLicenseChange(it))
                    },
                    label = "Driver license",
                    expanded = state.selectingDriverLicense,
                    onExpandedChange = {
                        onAction(AddEditVehicleAction.ToggleDriverLicenseDialog)
                    },
                    options = listOf("A1", "A2", "A", "AM")
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    value = state.spz,
                    onValueChange = {
                        onAction(AddEditVehicleAction.OnSpzChange(it))
                    },
                    label = {
                        Text("SPZ")
                    },
                    error = state.spzError,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        autoCorrectEnabled = false,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(focusDirection = FocusDirection.Next)
                        }
                    )
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Is it a transferable SPZ?",
                    )
                    Switch(
                        modifier = Modifier,
                        checked = state.transferableSpz,
                        onCheckedChange = {
                            onAction(AddEditVehicleAction.OnTransferableSpzChange)
                        }
                    )
                }
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    value = state.totalDistance,
                    onValueChange = {
                        onAction(AddEditVehicleAction.OnTotalDistanceChange(it))
                    },
                    label = {
                        Text("Total distance")
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        autoCorrectEnabled = false,
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Number
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(focusDirection = FocusDirection.Next)
                        }
                    ),
                    trailingIcon = {
                        Text(
                            text = "km",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    value = state.maximumDistance,
                    onValueChange = {
                        onAction(AddEditVehicleAction.OnMaximumDistanceChange(it))
                    },
                    label = {
                        Text("Maximum distance")
                    },
                    error = state.maximumDistanceError,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        autoCorrectEnabled = false,
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Number
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(focusDirection = FocusDirection.Next)
                        }
                    ),
                    trailingIcon = {
                        Text(
                            text = "km",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                )
                ChoiceTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    value = state.place,
                    onValueChange = {
                        onAction(AddEditVehicleAction.OnPlaceChange(it))
                    },
                    label = "Place",
                    expanded = state.selectingPlace,
                    onExpandedChange = {
                        onAction(AddEditVehicleAction.TogglePlaceDialog)
                    },
                    options = listOf("Ostrava","Olomouc","Zlín")
                )
            }
            item {
                PrimaryButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    onClick = {
                        onAction(AddEditVehicleAction.OnSaveClick)
                    },
                    text = "Create vehicle",
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add vehicle",
                        )
                    }
                )
            }
        }
    }

    if (state.imageDialog){
        cz.kudladev.vehicletracking.core.designsystem.ModalBottomSheet(
            onDismissRequest = {
                onAction(AddEditVehicleAction.ToggleImageDialog)
            }
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "You can get the images from the shop link",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    fontStyle = FontStyle.Italic
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.vehicleURL,
                    onValueChange = {
                        onAction(AddEditVehicleAction.OnVehicleURLChange(it))
                    },
                    label = {
                        Text("Vehicle shop link")
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        autoCorrectEnabled = false,
                        imeAction = ImeAction.Go
                    ),
                    keyboardActions = KeyboardActions(
                        onGo = {
                            onAction(AddEditVehicleAction.OnVehicleURLConfirm)
                            focusManager.clearFocus()
                            onAction(AddEditVehicleAction.ToggleImageDialog)
                        }
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                    )
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = "or"
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                    )
                }
                SecondaryButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(OutlinedTextFieldDefaults.MinHeight),
                    onClick = {
                        multipleImagePicker.launch()
                    },
                    text = "Pick from gallery",
                )
            }
        }
    }


    if (uploadStatus.isNotEmpty()) {
        UploadDialog(
            images = uploadStatus.size,
            uploadedImages = uploadStatus.count { it.state == ImageUploadState.COMPLETED }.toFloat(),
        )
    }
}

@Preview
@Composable
fun AddEditVehicleScreenPreview() {
    AppTheme {
        AddEditVehicleScreen(
            state = AddEditVehicleState(),
            uploadStatus = emptyList(),
            onAction = {},
            paddingValues = PaddingValues(0.dp),
            onBack = {}
        )
    }
}
