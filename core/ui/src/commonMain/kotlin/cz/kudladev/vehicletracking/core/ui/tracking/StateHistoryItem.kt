package cz.kudladev.vehicletracking.core.ui.tracking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cz.kudladev.vehicletracking.core.designsystem.theme.AppTheme
import cz.kudladev.vehicletracking.core.ui.util.toFormattedString
import cz.kudladev.vehicletracking.model.TrackingLog
import cz.kudladev.vehicletracking.model.TrackingState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun StateHistoryItem(
    modifier: Modifier = Modifier,
    trackingLog: TrackingLog,
    number: Int,
    isLast: Boolean = false,
){
    val lineColor = MaterialTheme.colorScheme.outline

    Row(
        modifier = Modifier
            .drawBehind{
                if (!isLast) {
                    val circleRadius = 28.dp.toPx()
                    val linePadding = circleRadius / 2
                    val lineStart = circleRadius + 8.dp.toPx()
                    drawLine(
                        color = lineColor,
                        start = Offset(linePadding, lineStart),
                        end = Offset(linePadding, size.height),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Square
                    )
                }
            }
            .then(modifier),
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
                    StateIcon(
                        state = trackingLog.state,
                        isLast = isLast
                    )
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
                modifier = Modifier.padding(start = 44.dp, top = 4.dp),
                text = trackingLog.message ?: "No message provided...",
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
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

@Composable
private fun StateIcon(
    isLast: Boolean,
    state: TrackingState
){
    Icon(
        imageVector = if (isLast) state.activeIcon else state.inactiveIcon,
        contentDescription = state.displayName,
        tint = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.size(28.dp)
    )
}

@Preview
@Composable
fun StateHistoryItemPreview() {
    AppTheme {
        Surface {
            StateHistoryItem(
                trackingLog = trackingLogs.first(),
                number = 1,
                isLast = false
            )
        }
    }
}