package cz.kudladev.vehicletracking.core.ui.tracking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer
import cz.kudladev.vehicletracking.core.designsystem.theme.AppTheme
import cz.kudladev.vehicletracking.model.Role
import cz.kudladev.vehicletracking.model.TrackingLog
import cz.kudladev.vehicletracking.model.TrackingState
import cz.kudladev.vehicletracking.model.User
import cz.kudladev.vehicletracking.model.getNextState
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalComposeApi::class)
@Composable
fun StateHistory(
    modifier: Modifier = Modifier,
    logs: List<TrackingLog>
){
    val last = logs.last().state.getNextState()
    val temp = if (last != null) { logs + TrackingLog(state = last, message = last.message) } else { logs }
    Column(
        modifier = modifier,
    ) {
        Text(
            modifier = Modifier.padding(bottom = 8.dp),
            text = "State History",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            fontStyle = FontStyle.Italic,
        )
        temp.forEachIndexed { index, log ->
            StateHistoryItem(
                modifier = Modifier.padding(bottom = 8.dp),
                trackingLog = log,
                number = index + 1,
                isLast = index == temp.lastIndex,
            )
        }
    }
}

@Composable
fun StateHistorySkeleton(
    modifier: Modifier = Modifier,
    itemCount: Int = 3
) {
    val titleHeight = with(LocalDensity.current) {
        MaterialTheme.typography.titleLarge.lineHeight.toDp()
    }

    Column(
        modifier = modifier.shimmer(),
    ) {
        // Title placeholder
        Box(
            modifier = Modifier
                .width(150.dp)
                .height(titleHeight)
                .padding(bottom = 8.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(Color.Gray)
        )

        // History items
        repeat(itemCount) { index ->
            StateHistoryItemSkeleton(
                modifier = Modifier.padding(bottom = 8.dp),
                isLast = index == itemCount - 1
            )
        }
    }
}

@Preview
@Composable
fun StateHistorySkeletonPreview() {
    AppTheme {
        Surface {
            StateHistorySkeleton()
        }
    }
}

@Preview
@Composable
fun StateHistoryPreview() {
    AppTheme {
        Surface {
            StateHistory(
                logs = trackingLogs
            )
        }
    }
}