package cz.kudladev.vehicletracking.core.ui.tracking

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cz.kudladev.vehicletracking.core.designsystem.Badge
import cz.kudladev.vehicletracking.core.designsystem.Image
import cz.kudladev.vehicletracking.core.designsystem.theme.AppTheme
import cz.kudladev.vehicletracking.core.ui.util.toFormattedString
import cz.kudladev.vehicletracking.model.Tracking
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import vehicletracking.core.ui.generated.resources.Res
import vehicletracking.core.ui.generated.resources.history
import vehicletracking.core.ui.generated.resources.historyEmpty

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
            text = stringResource(Res.string.history),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            fontStyle = FontStyle.Italic,
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (trackingHistory.isEmpty()) {
            Text(
                text = stringResource(Res.string.historyEmpty),
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
    val lastTracking = tracking.stateLogs.last()

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
        Box(
            modifier = Modifier
                .width(150.dp)
                .clip(MaterialTheme.shapes.medium)
        ){
            Image(
                imageUrl = tracking.vehicle.images.firstOrNull()?.url ?: "",
                modifier = Modifier
                    .width(150.dp)
                    .clip(MaterialTheme.shapes.medium)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp)
            ){
                Badge(
                    text = stringResource(lastTracking.state.displayName),
                    icon = lastTracking.state.activeIcon,
                )
            }
        }
        Column(
            modifier = Modifier
                .weight(1f),
        ) {
            Text(
                text = tracking.vehicle.fullName,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                minLines = 2
            )
            Text(
                text = tracking.endTime.toFormattedString()
            )
            tracking.finalDistance?.let {
                Text(
                    text = "+${it} km",
                )
            }
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