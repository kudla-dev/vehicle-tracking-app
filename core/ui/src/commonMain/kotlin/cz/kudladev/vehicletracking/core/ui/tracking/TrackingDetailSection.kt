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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cz.kudladev.vehicletracking.core.designsystem.theme.AppTheme
import cz.kudladev.vehicletracking.core.ui.util.toFormattedLongString
import kotlinx.datetime.LocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import vehicletracking.core.ui.generated.resources.Res
import vehicletracking.core.ui.generated.resources.endDate
import vehicletracking.core.ui.generated.resources.startDate
import vehicletracking.core.ui.generated.resources.trackingDetails
import vehicletracking.core.ui.generated.resources.trackingId

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
            text = stringResource(Res.string.trackingDetails),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            fontStyle = FontStyle.Italic,
        )
        Spacer(modifier = Modifier.height(24.dp))
        TrackingDetailSectionItem(stringResource(Res.string.trackingId), trackingId)
        TrackingDetailSectionItem(stringResource(Res.string.startDate), startDate.toFormattedLongString())
        TrackingDetailSectionItem(stringResource(Res.string.endDate), endDate.toFormattedLongString())
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
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
    )
    Row(
        modifier = Modifier.padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
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