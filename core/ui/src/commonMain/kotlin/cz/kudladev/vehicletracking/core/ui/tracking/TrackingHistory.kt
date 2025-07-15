package cz.kudladev.vehicletracking.core.ui.tracking

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cz.kudladev.vehicletracking.core.designsystem.Image
import cz.kudladev.vehicletracking.core.designsystem.theme.AppTheme
import cz.kudladev.vehicletracking.core.ui.util.toFormattedString
import cz.kudladev.vehicletracking.model.Tracking
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TrackingHistory(
    modifier: Modifier = Modifier,
    trackingHistory: List<Tracking>,
    onTrackingClick: (Tracking) -> Unit
){
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "History",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (trackingHistory.isEmpty()) {
            Text(
                text = "No tracking history available.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        } else {
            trackingHistory.forEach { tracking ->
                TrackingHistoryItem(
                    tracking = tracking,
                    onClick = onTrackingClick
                )
            }
        }
    }
}

@Composable
fun TrackingHistoryItem(tracking: Tracking, onClick: (Tracking) -> Unit) {
    Row(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable(
                onClick = {
                    onClick(tracking)
                },
            ),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Image(
            imageUrl = tracking.vehicle.images.firstOrNull()?.url ?: "",
            modifier = Modifier
                .width(150.dp)
                .clip(MaterialTheme.shapes.medium)
        )
        Column(
            modifier = Modifier
                .weight(1f),
        ) {
            Text(
                text = "${tracking.stateLogs.last().state.displayName}: ${tracking.endTime.toFormattedString()}"
            )
            Text(
                text = tracking.vehicle.fullName,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                minLines = 2
            )
            Text(
                text = "+${tracking.finalDistance} km",
            )
        }

    }
}

@Preview
@Composable
private fun TrackingHistoryPreview() {
    AppTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            TrackingHistory(
                modifier = Modifier.fillMaxWidth(),
                trackingHistory = listOf(testTracking,testTracking,testTracking,testTracking,testTracking),
                onTrackingClick = {}
            )
        }
    }
}