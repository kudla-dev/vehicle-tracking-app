package cz.kudladev.vehicletracking.core.ui.tracking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cz.kudladev.vehicletracking.core.designsystem.theme.AppTheme
import cz.kudladev.vehicletracking.core.ui.util.toFormattedString
import cz.kudladev.vehicletracking.model.TrackingLog
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun StateHistoryItem(
    modifier: Modifier = Modifier,
    trackingLog: TrackingLog,
    number: Int,
    isLast: Boolean = false,
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    StateHistoryItemNumber(number,isLast)
                    Column {
                        Text(
                            modifier = Modifier.padding(start = 16.dp),
                            text = trackingLog.state.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = if (isLast) FontWeight.Bold else FontWeight.Normal,
                        )
                        trackingLog.assignedAt?.toFormattedString()?.let {
                            Text(
                                modifier = Modifier.padding(start = 16.dp),
                                text = it,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
            Text(
                modifier = Modifier.padding(start = 64.dp),
                text = trackingLog.message ?: "No message provided...",
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun StateHistoryItemNumber(
    number: Int,
    isLast: Boolean
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(100.dp))
            .background(
                when (isLast) {
                    true -> {
                        MaterialTheme.colorScheme.primary
                    }
                    false -> {
                        MaterialTheme.colorScheme.inversePrimary
                    }
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = number.toString(),
            style = MaterialTheme.typography.titleSmall,
            color = when (isLast){
                true -> {
                    MaterialTheme.colorScheme.onPrimary
                }
                false -> {
                    MaterialTheme.colorScheme.onBackground
                }
            }
        )
    }
}

@Preview
@Composable
fun StateHistoryItemPreview() {
    AppTheme {
        StateHistoryItem(
            trackingLog = trackingLogs.first(),
            number = 1,
            isLast = true
        )
    }
}