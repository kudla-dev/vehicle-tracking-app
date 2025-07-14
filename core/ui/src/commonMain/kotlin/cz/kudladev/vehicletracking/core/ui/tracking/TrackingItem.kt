package cz.kudladev.vehicletracking.core.ui.tracking

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cz.kudladev.vehicletracking.core.designsystem.Image
import cz.kudladev.vehicletracking.core.designsystem.theme.AppTheme
import cz.kudladev.vehicletracking.core.ui.user.testUser
import cz.kudladev.vehicletracking.core.ui.util.toFormattedString
import cz.kudladev.vehicletracking.core.ui.vehicle.testVehicle
import cz.kudladev.vehicletracking.model.Tracking
import kotlinx.datetime.LocalDateTime
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TrackingItem(
    modifier: Modifier = Modifier,
    tracking: Tracking,
    onClick: () -> Unit,

){
    val lastTrackingState = tracking.stateLogs.lastOrNull()?.state
    Row(
        modifier = modifier,
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
        Column {
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

@Preview
@Composable
private fun TrackingItemPreview(){
    AppTheme {
        TrackingItem(
            modifier = Modifier.fillMaxWidth(),
            tracking = Tracking(
                id = "1",
                vehicle = testVehicle,
                user = testUser,
                startTime = LocalDateTime(2023, 10, 1, 12, 0, 0),
                endTime = LocalDateTime(2023, 10, 1, 14, 0, 0),
                startTachometer = 0,
                endTachometer = 0,
                finalDistance = 0,
                stateLogs = trackingLogs,
            ),
            onClick = {
                
            }
        )
    }
}