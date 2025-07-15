package cz.kudladev.vehicletracking.core.ui.tracking

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Current State",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = currentTracking.state.displayName,
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = currentTracking.assignedAt?.toFormattedString() ?: "N/A",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
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
                currentTracking = TrackingLog(
                    trackingId = "f66c288c-eb50-4743-8f5a-cfb4eb08344d",
                    state = TrackingState.PENDING,
                    message = "Reservation for vehicle has been created",
                    assignedBy = User(
                        id = "123",
                        firstName = "John",
                        lastName = "Doe",
                        fullName = "John Doe",
                        email = "john.doe@seznam.cz",
                        phoneNumber = "+420123456789",
                        role = Role.ADMIN,
                        maximumDistance = 100,
                        overallDistance = 0
                    ),
                    assignedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                    images = emptyList(),
                )
            )
        }
    }
}