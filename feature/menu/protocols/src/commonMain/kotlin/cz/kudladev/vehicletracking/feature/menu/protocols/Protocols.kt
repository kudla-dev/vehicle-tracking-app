package cz.kudladev.vehicletracking.feature.menu.protocols

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
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
import cz.kudladev.vehicletracking.core.ui.image.SummaryImageSectionItem
import cz.kudladev.vehicletracking.core.ui.nextString
import cz.kudladev.vehicletracking.core.ui.submitString
import cz.kudladev.vehicletracking.core.ui.util.toImageBitmap
import cz.kudladev.vehicletracking.model.*
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import vehicletracking.feature.menu.protocols.generated.resources.Res
import vehicletracking.feature.menu.protocols.generated.resources.additionalNotes
import vehicletracking.feature.menu.protocols.generated.resources.cameraPermissionDenied
import vehicletracking.feature.menu.protocols.generated.resources.currentMileage
import vehicletracking.feature.menu.protocols.generated.resources.pickUpProtocolTitle
import vehicletracking.feature.menu.protocols.generated.resources.retake
import vehicletracking.feature.menu.protocols.generated.resources.returnProtocolTitle

@Serializable
data class Protocols(
    val type: ProtocolsType,
    val trackingId: String,
    val trackingState: TrackingState,
)

@Serializable
enum class ProtocolsType(val title: StringResource) {
    PICKUP(Res.string.pickUpProtocolTitle),
    RETURN(Res.string.returnProtocolTitle),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProtocolsRoot(
    viewModel: ProtocolsViewModel = koinViewModel(),
    bottomAppBarScrollBehavior: BottomAppBarScrollBehavior,
    type: ProtocolsType,
    paddingValues: PaddingValues,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val uploadStatus by viewModel.uploadStatus.collectAsStateWithLifecycle()

    LaunchedEffect(state.updatedTracking) {
        println("Upload status changed: $uploadStatus and result state: ${state.updatedTracking}")
//        uploadStatus.isNotEmpty() && uploadStatus.size == state.images.size && uploadStatus.all { status -> status is ImageUploadState.Completed } &&
        if (state.updatedTracking is UiState.Success) {
//            viewModel.onAction(ProtocolsAction.ClearRecentUploads)
            onBack()
        }
    }

    ProtocolsScreen(
        state = state,
        bottomAppBarScrollBehavior = bottomAppBarScrollBehavior,
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
    bottomAppBarScrollBehavior: BottomAppBarScrollBehavior,
    onAction: (ProtocolsAction) -> Unit,
    type: ProtocolsType,
    paddingValues: PaddingValues,
    onBack: () -> Unit,
    uploadStatus: List<ImageUploadState>,
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

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
                        text = stringResource(type.title)
                    )
                },
                navigationIcon = {
                    BackButton(
                        onClick = onBack
                    )
                },
                scrollBehavior = topAppBarScrollBehavior
            )
        },
        contentWindowInsets = WindowInsets(0.dp),
        modifier = Modifier
            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
            .nestedScroll(bottomAppBarScrollBehavior.nestedScrollConnection)
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

//    if (uploadStatus.isNotEmpty()) {
//        UploadDialog(
//            images = uploadStatus.size,
//            uploadedImages = uploadStatus.count { it is ImageUploadState.Completed }.toFloat(),
//        )
//    }
}

@Composable
fun ProtocolsCapture(
    cameraState: PeekabooCameraState,
    page: ProtocolsScreenPage,
    capturedImage: ImageUploadState?,
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
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ){
            Text(
                text = page.instruction,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )
        }
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ){
            AnimatedContent(
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
                                        stringResource(Res.string.cameraPermissionDenied),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            },
                        )
                    }
                    else -> {
                        capturedImage?.let { capturedImage ->
                            if (capturedImage is ImageUploadState.Completed) {
                                val imageModifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .aspectRatio(16f/9f)
                                    .clip(MaterialTheme.shapes.medium)

                                when (val imageURL = capturedImage.imageURL) {
                                    is ImageWithUrl -> {
                                        CoilImage(
                                            imageModel = { imageURL.url },
                                            modifier = imageModifier,
                                            imageOptions = ImageOptions(
                                                contentScale = ContentScale.Crop,
                                            )
                                        )
                                    }
                                    is ImageWithBytes -> {
                                        val bitmap = imageURL.bytes?.toImageBitmap()
                                        if (bitmap != null) {
                                            Image(
                                                bitmap = bitmap,
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
                }
            }
        }
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ){
            AnimatedContent(
                targetState = capturedImage,
            ){
                when (it) {
                    null -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ){
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
                    }
                    else -> {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = {
                                    onRetake()
                                }
                            ){
                                Text(
                                    stringResource(Res.string.retake),
                                )
                            }
                            Button(
                                onClick = {
                                    onNext()
                                }
                            ){
                                Text(
                                    nextString(),
                                )
                            }
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

    val beforeImages by remember{
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
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(16.dp),
    ) {
        itemsIndexed(ProtocolsScreenPage.viewPages()) { index,page ->
            SummaryImageSectionItem(
                beforeImage = beforeImages?.get(index) ?: state.images[page],
                afterImage = if (beforeImages != null) state.images[page] else null,
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
                        stringResource(Res.string.currentMileage),
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
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
                        stringResource(Res.string.additionalNotes),
                    )
                },
                modifier = Modifier
                    .fillMaxWidth(),
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
                PrimaryButton(
                    onClick = {
                        onSubmit()
                    },
                    text = submitString(),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = null
                        )
                    }
                )
            }
        }
        item {
            Spacer(
                modifier = Modifier.padding(WindowInsets.ime.asPaddingValues().calculateBottomPadding())
            )
        }
    }
}