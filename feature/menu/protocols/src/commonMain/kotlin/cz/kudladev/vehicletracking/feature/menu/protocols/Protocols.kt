package cz.kudladev.vehicletracking.feature.menu.protocols

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import cz.kudladev.vehicletracking.core.designsystem.BackButton
import cz.kudladev.vehicletracking.core.designsystem.LargeTopAppBar
import cz.kudladev.vehicletracking.core.designsystem.OutlinedTextField
import cz.kudladev.vehicletracking.core.ui.util.toImageBitmap
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data class Protocols(
    val type: ProtocolsType,
    val trackingId: String,
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

    ProtocolsScreen(
        state = state,
        onAction = viewModel::onAction,
        type = type,
        paddingValues = paddingValues,
        onBack = onBack
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
                    BackButton { onBack() }
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
                                // TODO: Handle submission logic here
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProtocolsCapture(
    cameraState: PeekabooCameraState,
    page: ProtocolsScreenPage,
    capturedImage: ByteArray?,
    onRetake: () -> Unit,
    action: (ProtocolsAction) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
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
                        Image(
                            bitmap = capturedImage.toImageBitmap(),
                            contentDescription = page.instruction,
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .aspectRatio(16f/9f)
                                .clip(MaterialTheme.shapes.medium),
                            contentScale = ContentScale.Crop,
                        )
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
                            .border(1.dp, Color.White, RoundedCornerShape(100))
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
                                .background(Color.White)
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
){
    val focusManager = LocalFocusManager.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(ProtocolsScreenPage.viewPages()){ page ->
            SummaryImage(
                byteArray = state.images[page] ?: ByteArray(0),
                page = page,
            )
        }
        item {
            OutlinedTextField(
                value = state.tachometerReading,
                onValueChange = {
                    if (it.lastOrNull()?.isDigit() ?: true){
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
            ){
                Button(
                    onClick = {

                    }
                ){
                    Text(
                        text = "Submit",
                    )
                }
            }
        }
    }
}

@Composable
fun SummaryImage(
    byteArray: ByteArray,
    page: ProtocolsScreenPage,
){
    Column {
        Text(
            text = page.title,
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Image(
            bitmap = byteArray.toImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .aspectRatio(16f/9f)
                .clip(MaterialTheme.shapes.medium),
            contentScale = ContentScale.Crop,
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}