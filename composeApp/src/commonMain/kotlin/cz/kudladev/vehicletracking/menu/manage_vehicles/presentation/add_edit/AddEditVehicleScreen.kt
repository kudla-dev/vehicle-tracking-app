package cz.kudladev.vehicletracking.menu.manage_vehicles.presentation.add_edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import cz.kudladev.vehicletracking.core.domain.models.Brand
import cz.kudladev.vehicletracking.core.presentation.components.basics.BackButton
import cz.kudladev.vehicletracking.core.presentation.components.basics.ChoiceTextField
import cz.kudladev.vehicletracking.core.presentation.components.basics.MediumTopBar
import cz.kudladev.vehicletracking.core.presentation.components.basics.TextField
import cz.kudladev.vehicletracking.core.presentation.components.vehicle.VehicleImages
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddEditVehicleRoot(
    viewModel: AddEditVehicleViewModel = koinViewModel(),
    paddingValues: PaddingValues,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    AddEditVehicleScreen(
        state = state,
        onAction = viewModel::onAction,
        paddingValues = paddingValues,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditVehicleScreen(
    state: AddEditVehicleState,
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


    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopBar(
                title = {
                    Text(
                        "Create new vehicle",
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
            start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
            top = innerPadding.calculateTopPadding() + 16.dp,
            end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
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
                    }
                )
                OutlinedButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp),
                    onClick = {
                        onAction(AddEditVehicleAction.ToggleImageDialog)
                    }
                ) {
                    Text(
                        "Add images",
                    )
                }
                state.imagesError?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(start = 24.dp),
                        softWrap = true,
                        maxLines = 2,
                        minLines = 1
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                ChoiceTextField<Brand>(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
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
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
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
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    value = state.name,
                    onValueChange = {
                        onAction(AddEditVehicleAction.OnNameChange(it))
                    },
                    error = state.nameError,
                    label = {
                        Text("Name")
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
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
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
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
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
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
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
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
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
                    modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, top = 4.dp),
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
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
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
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
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
                ChoiceTextField<String>(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
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
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    onClick = {
                        onAction(AddEditVehicleAction.OnSaveClick)
                    }
                ) {
                    Text(
                        text = "Create vehicle",
                    )
                }
            }
        }
    }

    if (state.imageDialog){
        ModalBottomSheet(
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
                    fontWeight = FontWeight.SemiBold
                )
                TextField(
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
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = "or"
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(OutlinedTextFieldDefaults.MinHeight),
                    onClick = {
                        multipleImagePicker.launch()
                    }
                ) {
                    Text(
                        text = "Pick from gallery",
                    )
                }
            }
        }
    }
}
