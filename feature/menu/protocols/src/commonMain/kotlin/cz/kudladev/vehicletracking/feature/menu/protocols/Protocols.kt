package cz.kudladev.vehicletracking.feature.menu.protocols

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.preat.peekaboo.ui.camera.CameraMode
import com.preat.peekaboo.ui.camera.PeekabooCamera
import com.preat.peekaboo.ui.camera.PeekabooCameraState
import com.preat.peekaboo.ui.camera.rememberPeekabooCameraState
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil3.CoilImage
import cz.kudladev.vehicletracking.core.designsystem.BackButton
import cz.kudladev.vehicletracking.core.designsystem.LargeTopAppBar
import cz.kudladev.vehicletracking.core.designsystem.OutlinedTextField
import cz.kudladev.vehicletracking.core.designsystem.PrimaryButton
import cz.kudladev.vehicletracking.core.ui.image.SummaryImage
import cz.kudladev.vehicletracking.core.ui.image.UploadDialog
import cz.kudladev.vehicletracking.core.ui.util.toImageBitmap
import cz.kudladev.vehicletracking.model.*
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data class Protocols(
    val type: ProtocolsType,
    val trackingId: String,
    val trackingState: TrackingState,
)

@Serializable
enum class ProtocolsType(val title: String) {
    PICKUP("Pickup Protocol"),
    RETURN("Return Protocol"),
}

@Composable
fun ProtocolsRoot(
    viewModel: ProtocolsViewModel = koinViewModel(),
    type: ProtocolsType,
    paddingValues: PaddingValues,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val uploadStatus by viewModel.uploadStatus.collectAsStateWithLifecycle()

    LaunchedEffect(uploadStatus, state.tracking) {
        println("Upload status changed: $uploadStatus and result state: ${state.tracking}")
        if (uploadStatus.isNotEmpty() && uploadStatus.size == state.images.size && uploadStatus.all { status -> status.state == ImageUploadState.COMPLETED } && state.tracking !is UiState.Error) {
            viewModel.onAction(ProtocolsAction.ClearRecentUploads)
            onBack()
        }
    }

    ProtocolsScreen(
        state = state,
        onAction = viewModel::onAction,
        type = type,
        paddingValues = paddingValues,
        onBack = onBack,
        uploadStatus = uploadStatus,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProtocolsScreen(
    state: ProtocolsState,
    onAction: (ProtocolsAction) -> Unit,
    type: ProtocolsType,
    paddingValues: PaddingValues,
    onBack: () -> Unit,
    uploadStatus: List<ImageUploadStatus>,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val cameraState = rememberPeekabooCameraState(
        onCapture = { byteArray ->
            byteArray?.let {
                onAction(ProtocolsAction.AddImage(
                    page = state.page,
                    image = it
                ))
            }
        },
        initialCameraMode = CameraMode.Back
    )

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = type.title
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
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        val combinedPadding = PaddingValues(
            bottom = paddingValues.calculateBottomPadding(),
            top = innerPadding.calculateTopPadding(),
            start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
            end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
        )
        Column(
            modifier = Modifier
                .padding(combinedPadding)
                .fillMaxSize()
        ) {
            Crossfade(
                targetState = state.page
            ){
                when (it) {
                    ProtocolsScreenPage.FRONT -> {
                        ProtocolsCapture(
                            cameraState = cameraState,
                            page = ProtocolsScreenPage.FRONT,
                            capturedImage = state.images[ProtocolsScreenPage.FRONT],
                            onRetake = {

                            },
                            action = onAction,
                            onBack = {
                                onAction(ProtocolsAction.PreviousPage(it))
                            },
                            onNext = {
                                onAction(ProtocolsAction.NextPage(it))
                            }
                        )
                    }
                    ProtocolsScreenPage.BACK -> {
                        ProtocolsCapture(
                            cameraState = cameraState,
                            page = ProtocolsScreenPage.BACK,
                            capturedImage = state.images[ProtocolsScreenPage.BACK],
                            onRetake = {

                            },
                            action = onAction,
                            onBack = {
                                onAction(ProtocolsAction.PreviousPage(it))
                            },
                            onNext = {
                                onAction(ProtocolsAction.NextPage(it))
                            }
                        )
                    }
                    ProtocolsScreenPage.LEFT -> {
                        ProtocolsCapture(
                            cameraState = cameraState,
                            page = ProtocolsScreenPage.LEFT,
                            capturedImage = state.images[ProtocolsScreenPage.LEFT],
                            onRetake = {

                            },
                            action = onAction,
                            onBack = {
                                onAction(ProtocolsAction.PreviousPage(it))
                            },
                            onNext = {
                                onAction(ProtocolsAction.NextPage(it))
                            }
                        )
                    }
                    ProtocolsScreenPage.RIGHT -> {
                        ProtocolsCapture(
                            cameraState = cameraState,
                            page = ProtocolsScreenPage.RIGHT,
                            capturedImage = state.images[ProtocolsScreenPage.RIGHT],
                            onRetake = {

                            },
                            action = onAction,
                            onBack = {
                                onAction(ProtocolsAction.PreviousPage(it))
                            },
                            onNext = {
                                onAction(ProtocolsAction.NextPage(it))
                            }
                        )
                    }
                    ProtocolsScreenPage.TACHOMETER -> {
                        ProtocolsCapture(
                            cameraState = cameraState,
                            page = ProtocolsScreenPage.TACHOMETER,
                            capturedImage = state.images[ProtocolsScreenPage.TACHOMETER],
                            onRetake = {

                            },
                            action = onAction,
                            onBack = {
                                onAction(ProtocolsAction.PreviousPage(it))
                            },
                            onNext = {
                                onAction(ProtocolsAction.NextPage(it))
                            }
                        )
                    }
                    ProtocolsScreenPage.SUMMARY -> {
                        SummaryProtocol(
                            state = state,
                            onAction = onAction,
                            onBack = {
                                onAction(ProtocolsAction.PreviousPage(it))
                            },
                            onSubmit = {
                                onAction(ProtocolsAction.SubmitTracking)
                            }
                        )
                    }
                }
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

@Composable
fun ProtocolsCapture(
    cameraState: PeekabooCameraState,
    page: ProtocolsScreenPage,
    capturedImage: Image?,
    onRetake: () -> Unit,
    action: (ProtocolsAction) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit,
){
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ){
        Text(
            text = page.instruction,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Crossfade(
            targetState = capturedImage
        ){
            when (it) {
                null -> {
                    PeekabooCamera(
                        state = cameraState,
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .aspectRatio(16f/9f)
                            .clip(MaterialTheme.shapes.medium),
                        permissionDeniedContent = {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    "Camera permission denied. Please allow camera access in settings.",
                                    textAlign = TextAlign.Center
                                )
                            }
                        },
                    )
                }
                else -> {
                    capturedImage?.let { capturedImage ->
                        val imageModifier = Modifier
                            .fillMaxWidth(0.9f)
                            .aspectRatio(16f/9f)
                            .clip(MaterialTheme.shapes.medium)
                        when (capturedImage) {
                            is ImageWithUrl -> {
                                CoilImage(
                                    imageModel = {
                                        capturedImage.url
                                    },
                                    modifier = imageModifier,
                                    imageOptions = ImageOptions(
                                        contentScale = ContentScale.Crop,
                                    )
                                )
                            }
                            is ImageWithBytes -> {
                                Image(
                                    bitmap = capturedImage.bytes?.toImageBitmap() ?: return@Crossfade,
                                    contentDescription = null,
                                    modifier = imageModifier,
                                    contentScale = ContentScale.Crop,
                                )
                            }
                        }
                    }
                }
            }
        }
        Crossfade(
            targetState = capturedImage
        ){
            when (it) {
                null -> {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(100))
                            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f), RoundedCornerShape(100))
                            .clickable(
                                onClick = {
                                    cameraState.capture()
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(100))
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                        )
                    }
                }
                else -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                    ) {
                        TextButton(
                            onClick = {
                                onRetake()
                            }
                        ){
                            Text(
                                "Retake",
                            )
                        }
                        Button(
                            onClick = {
                                onNext()
                            }
                        ){
                            Text(
                                "Next",
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryProtocol(
    state: ProtocolsState,
    onAction: (ProtocolsAction) -> Unit,
    onBack: () -> Unit,
    onSubmit: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    val nextImages by remember{
        mutableStateOf(
            when (state.tracking) {
                is UiState.Success -> {
                    state.tracking.data.stateLogs.firstOrNull() { it.state == TrackingState.ACTIVE }
                        ?.images
                }
                else -> emptyList()
            }
        )
    }


    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        itemsIndexed(ProtocolsScreenPage.viewPages()) { index,page ->
            SummaryImage(
                image = state.images[page],
                nextImage = nextImages?.getOrElse(index) { null },
                title = page.title,
            )
        }
        item {
            OutlinedTextField(
                value = state.tachometerReading,
                onValueChange = {
                    if (it.lastOrNull()?.isDigit() ?: true) {
                        onAction(ProtocolsAction.SetTachometerReading(it))
                    }
                },
                label = {
                    Text(
                        "Current Mileage",
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp, start = 8.dp, end = 8.dp),
                maxLines = 1,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                )
            )
        }
        item {
            OutlinedTextField(
                value = state.additionalNotes,
                onValueChange = {
                    onAction(ProtocolsAction.SetAdditionalNotes(it))
                },
                label = {
                    Text(
                        "Additional Notes",
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.clearFocus()
                    }
                )
            )
        }
        item {
            Box(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        onSubmit()
                    }
                ) {
                    Text(
                        text = "Submit",
                    )
                }
            }
        }
    }
}