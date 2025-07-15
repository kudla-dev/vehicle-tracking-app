package cz.kudladev.vehicletracking.core.ui.tracking

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cz.kudladev.vehicletracking.core.designsystem.theme.AppTheme
import cz.kudladev.vehicletracking.core.ui.util.toFormattedString
import kotlinx.datetime.LocalDateTime
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TrackingDetailSection(
    modifier: Modifier = Modifier,
    trackingId: String,
    startDate: LocalDateTime,
    endDate: LocalDateTime,
){
    Column(
        modifier = modifier,
    ) {
        Text(
            text = "Details",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(24.dp))
        TrackingDetailSectionItem("Tracking\nID", trackingId)
        TrackingDetailSectionItem("Start Date", startDate.toFormattedString())
        TrackingDetailSectionItem("End Date", endDate.toFormattedString())
    }
}

@Composable
private fun TrackingDetailSectionItem(
    title: String,
    value: String
) {
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth(),
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.outline,
    )
    Row(
        modifier = Modifier.padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
        )
        Text(
            modifier = Modifier.weight(3f),
            text = value,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}


@Preview
@Composable
fun TrackingDetailSectionPreview() {
    AppTheme {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
        ) {
            TrackingDetailSection(
                trackingId = "12345",
                startDate = LocalDateTime(2023, 10, 1, 12, 0),
                endDate = LocalDateTime(2023, 10, 2, 12, 0),
            )
        }
    }
}