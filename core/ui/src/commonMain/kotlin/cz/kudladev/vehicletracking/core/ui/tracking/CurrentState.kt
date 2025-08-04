package cz.kudladev.vehicletracking.core.ui.tracking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer
import cz.kudladev.vehicletracking.core.designsystem.theme.AppTheme
import cz.kudladev.vehicletracking.core.ui.util.toFormattedString
import cz.kudladev.vehicletracking.model.Role
import cz.kudladev.vehicletracking.model.TrackingLog
import cz.kudladev.vehicletracking.model.TrackingState
import cz.kudladev.vehicletracking.model.User
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CurrentState(
    modifier: Modifier = Modifier,
    currentTracking: TrackingLog,
){
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Current State",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            fontStyle = FontStyle.Italic,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = currentTracking.state.displayName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = currentTracking.assignedAt?.toFormattedString() ?: "N/A",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
        when (currentTracking.state){
            TrackingState.PENDING -> {

            }
            TrackingState.APPROVED -> {

            }
            TrackingState.ACTIVE -> {

            }
            TrackingState.RETURNED -> {

            }
            TrackingState.REJECTED -> {

            }
            TrackingState.FAILED -> {

            }
            TrackingState.COMPLETED -> {

            }
            TrackingState.ERROR -> {

            }
            else -> {

            }
        }
    }
}

@Composable
fun CurrentStateSkeleton(
    modifier: Modifier = Modifier
) {
    val titleHeight = with(LocalDensity.current) {
        MaterialTheme.typography.titleLarge.lineHeight.toDp()
    }
    val bodyHeight = with(LocalDensity.current) {
        MaterialTheme.typography.bodyLarge.lineHeight.toDp()
    }

    Column(
        modifier = modifier.shimmer(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Title placeholder
        Box(
            modifier = Modifier
                .width(150.dp)
                .height(titleHeight)
                .clip(MaterialTheme.shapes.medium)
                .background(Color.Gray)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // State name placeholder
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(bodyHeight)
                    .clip(MaterialTheme.shapes.medium)
                    .background(Color.Gray)
            )

            // Date placeholder
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(bodyHeight)
                    .clip(MaterialTheme.shapes.medium)
                    .background(Color.Gray)
            )
        }

        // We don't need placeholders for the when statement content
        // as it appears to be empty in the original implementation
    }
}

@Preview
@Composable
fun CurrentStateSkeletonPreview() {
    AppTheme {
        Surface {
            CurrentStateSkeleton(
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
fun CurrentStatePreview() {
    AppTheme {
        Surface {
            CurrentState(
                currentTracking = testTracking.stateLogs.last()
            )
        }
    }
}