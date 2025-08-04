package cz.kudladev.vehicletracking.core.ui.tracking

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer
import cz.kudladev.vehicletracking.core.designsystem.Image
import cz.kudladev.vehicletracking.core.designsystem.theme.AppTheme
import cz.kudladev.vehicletracking.core.ui.user.testUser
import cz.kudladev.vehicletracking.core.ui.vehicle.testVehicle
import cz.kudladev.vehicletracking.model.Role
import cz.kudladev.vehicletracking.model.Tracking
import cz.kudladev.vehicletracking.model.TrackingLog
import cz.kudladev.vehicletracking.model.TrackingState
import cz.kudladev.vehicletracking.model.User
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TrackingItem(
    modifier: Modifier = Modifier,
    tracking: Tracking,
    onClick: () -> Unit,
){
    val lastTrackingState = tracking.stateLogs.lastOrNull()?.state
    Card(
        modifier = modifier,
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(20.dp)
    ){
        Row(
            modifier = Modifier
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                imageUrl = tracking.vehicle.images.firstOrNull()?.url ?: "",
                modifier = Modifier
                    .width(160.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .aspectRatio(16f / 9f),
                contentScale = ContentScale.Crop,
            )
            Column(
                modifier = Modifier
                    .padding(4.dp)
            ) {
                lastTrackingState?.let {
                    Text(
                        text = it.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = tracking.vehicle.fullName,
                        style = MaterialTheme.typography.bodyMedium,
                        minLines = 2,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = tracking.user.fullName,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
fun TrackingItemSkeleton(
    modifier: Modifier = Modifier
) {
    val titleHeight = with(LocalDensity.current) {
        MaterialTheme.typography.titleMedium.lineHeight.toDp()
    }
    val bodyHeight = with(LocalDensity.current) {
        MaterialTheme.typography.bodyMedium.lineHeight.toDp()
    }

    Card(
        modifier = Modifier
            .shimmer()
            .then(modifier),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Image placeholder
            Box(
                modifier = Modifier
                    .width(160.dp)
                    .aspectRatio(16f / 9f)
                    .clip(MaterialTheme.shapes.medium)
                    .background(Color.Gray)
            )

            Column(
                modifier = Modifier
                    .padding(4.dp)
                    .weight(1f)
            ) {
                // Title placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(titleHeight)
                        .clip(MaterialTheme.shapes.medium)
                        .background(Color.Gray)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Vehicle name placeholder (2 lines)
                repeat(2) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(bodyHeight)
                            .clip(MaterialTheme.shapes.medium)
                            .background(Color.Gray)
                    )

                    if (it == 0) {
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // User name placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(bodyHeight)
                        .clip(MaterialTheme.shapes.medium)
                        .background(Color.Gray)
                )
            }
        }
    }
}

internal val trackingLogs = listOf(
    TrackingLog(
        trackingId = "f66c288c-eb50-4743-8f5a-cfb4eb08344d",
        state = TrackingState.PENDING,
        message = "Reservation for vehicle has been created",
        assignedBy = User(
            id = "f66c288c-eb50-4743-8f5a-cfb4eb08344d",
            firstName = "John",
            lastName = "Doe",
            fullName = "John Doe",
            email = "john.doe@seznam.cz",
            phoneNumber = "+420123456789",
            role = Role.ADMIN,
            maximumDistance = 100,
            overallDistance = 0
        ),
        assignedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
        images = emptyList(),
    ),
    TrackingLog(
        trackingId = "f66c288c-eb50-4743-8f5a-cfb4eb08344d",
        state = TrackingState.APPROVED,
        message = "Reservation for vehicle has been created",
        assignedBy = User(
            id = "f66c288c-eb50-4743-8f5a-cfb4eb08344d",
            firstName = "John",
            lastName = "Doe",
            fullName = "John Doe",
            email = "john.doe@seznam.cz",
            phoneNumber = "+420123456789",
            role = Role.ADMIN,
            maximumDistance = 100,
            overallDistance = 0
        ),
        assignedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
        images = emptyList(),
    ),
    TrackingLog(
        trackingId = "f66c288c-eb50-4743-8f5a-cfb4eb08344d",
        state = TrackingState.ACTIVE,
        message = "Reservation for vehicle has been created",
        assignedBy = User(
            id = "f66c288c-eb50-4743-8f5a-cfb4eb08344d",
            firstName = "John",
            lastName = "Doe",
            fullName = "John Doe",
            email = "john.doe@seznam.cz",
            phoneNumber = "+420123456789",
            role = Role.ADMIN,
            maximumDistance = 100,
            overallDistance = 0
        ),
        assignedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
        images = emptyList(),
    ),
    TrackingLog(
        trackingId = "f66c288c-eb50-4743-8f5a-cfb4eb08344d",
        state = TrackingState.RETURNED,
        message = "Reservation for vehicle has been created",
        assignedBy = User(
            id = "f66c288c-eb50-4743-8f5a-cfb4eb08344d",
            firstName = "John",
            lastName = "Doe",
            fullName = "John Doe",
            email = "john.doe@seznam.cz",
            phoneNumber = "+420123456789",
            role = Role.ADMIN,
            maximumDistance = 100,
            overallDistance = 0
        ),
        assignedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
        images = emptyList(),
    ),
//    TrackingLog(
//        trackingId = "f66c288c-eb50-4743-8f5a-cfb4eb08344d",
//        state = TrackingState.COMPLETED,
//        message = "Reservation for vehicle has been created",
//        assignedBy = User(
//            firstName = "John",
//            lastName = "Doe",
//            fullName = "John Doe",
//            email = "john.doe@seznam.cz",
//            phoneNumber = "+420123456789",
//            role = Role.ADMIN,
//            maximumDistance = 100,
//            overallDistance = 0
//        ),
//        assignedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
//        images = emptyList(),
//    )
)

internal val testTracking = Tracking(
    id = "1",
    vehicle = testVehicle,
    user = testUser,
    startTime = LocalDateTime(2023, 10, 1, 12, 0, 0),
    endTime = LocalDateTime(2023, 10, 1, 14, 0, 0),
    startTachometer = 0,
    endTachometer = 0,
    finalDistance = 0,
    stateLogs = trackingLogs,
)

@Preview()
@Composable
private fun TrackingItemPreview(){
    AppTheme {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
        ) {
            TrackingItem(
                modifier = Modifier.fillMaxWidth(),
                tracking = testTracking,
                onClick = {

                }
            )
        }
    }
}

@Preview
@Composable
private fun TrackingItemSkeletonPreview() {
    AppTheme {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
        ) {
            TrackingItemSkeleton(
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}