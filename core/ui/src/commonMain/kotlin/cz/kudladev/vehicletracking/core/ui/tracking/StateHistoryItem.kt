package cz.kudladev.vehicletracking.core.ui.tracking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer
import cz.kudladev.vehicletracking.core.designsystem.theme.AppTheme
import cz.kudladev.vehicletracking.core.ui.util.toFormattedLongString
import cz.kudladev.vehicletracking.model.Tracking
import cz.kudladev.vehicletracking.model.TrackingLog
import cz.kudladev.vehicletracking.model.TrackingState
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import vehicletracking.core.ui.generated.resources.Res
import vehicletracking.core.ui.generated.resources.noMessageProvided

@Composable
fun StateHistoryItem(
    modifier: Modifier = Modifier,
    tracking: Tracking,
    trackingLog: TrackingLog,
    number: Int,
    isLast: Boolean = false,
){
    val lineColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)

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
                            text = stringResource(trackingLog.state.displayName),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = if (isLast) FontWeight.Bold else FontWeight.Normal,
                        )
                        trackingLog.assignedAt?.toFormattedLongString()?.let {
                            Text(
                                modifier = Modifier.padding(start = 16.dp),
                                text = it,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
            when (trackingLog.message){
                null, "" -> {
                    when(trackingLog.state.message){
                        null -> stringResource(Res.string.noMessageProvided)
                        else -> stringResource(trackingLog.state.message!!,
                            when (trackingLog.state) {
                                TrackingState.WAITING_FOR_START -> tracking.startTime.toFormattedLongString()
                                TrackingState.WAITING_FOR_RETURN -> tracking.endTime.toFormattedLongString()
                                else -> ""
                            }
                        )
                    }
                }
                else -> trackingLog.message
            }?.let {
                Text(
                    modifier = Modifier.padding(start = 44.dp, top = 4.dp),
                    text = it,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun StateIcon(
    isLast: Boolean,
    state: TrackingState
){
    Icon(
        imageVector = if (isLast) state.activeIcon else state.inactiveIcon,
        contentDescription = stringResource(state.displayName),
        tint = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.size(28.dp)
    )
}

@Composable
fun StateHistoryItemSkeleton(
    modifier: Modifier = Modifier,
    isLast: Boolean = false,
) {
    val lineColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
    val titleHeight = with(LocalDensity.current) {
        MaterialTheme.typography.titleMedium.lineHeight.toDp()
    }
    val labelHeight = with(LocalDensity.current) {
        MaterialTheme.typography.labelSmall.lineHeight.toDp()
    }
    val bodyHeight = with(LocalDensity.current) {
        MaterialTheme.typography.bodyMedium.lineHeight.toDp()
    }

    Row(
        modifier = Modifier
            .shimmer()
            .drawBehind {
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
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // State icon placeholder
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color.Gray)
                    )

                    Column {
                        // State name placeholder
                        Box(
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .width(120.dp)
                                .height(titleHeight)
                                .clip(MaterialTheme.shapes.medium)
                                .background(Color.Gray)
                        )

                        // Date placeholder
                        Box(
                            modifier = Modifier
                                .padding(start = 16.dp, top = 4.dp)
                                .width(80.dp)
                                .height(labelHeight)
                                .clip(MaterialTheme.shapes.medium)
                                .background(Color.Gray)
                        )
                    }
                }
            }

            // Message placeholder
            Box(
                modifier = Modifier
                    .padding(start = 44.dp, top = 4.dp)
                    .fillMaxWidth(0.8f)
                    .height(bodyHeight)
                    .clip(MaterialTheme.shapes.medium)
                    .background(Color.Gray)
            )
        }
    }
}

@Preview
@Composable
fun StateHistoryItemSkeletonPreview() {
    AppTheme {
        Surface {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StateHistoryItemSkeleton(isLast = false)
                StateHistoryItemSkeleton(isLast = false)
                StateHistoryItemSkeleton(isLast = true)
            }
        }
    }
}

@Preview
@Composable
fun StateHistoryItemPreview() {
    AppTheme {
        Surface {
            StateHistoryItem(
                tracking = testTracking,
                trackingLog = trackingLogs.first(),
                number = 1,
                isLast = false
            )
        }
    }
}