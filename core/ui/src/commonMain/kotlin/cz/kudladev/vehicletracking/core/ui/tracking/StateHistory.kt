package cz.kudladev.vehicletracking.core.ui.tracking

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
            fontWeight = FontWeight.SemiBold
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

internal val trackingLogs = listOf(
    TrackingLog(
        trackingId = "f66c288c-eb50-4743-8f5a-cfb4eb08344d",
        state = TrackingState.PENDING,
        message = "Reservation for vehicle has been created",
        assignedBy = User(
            id = "f66c288c-eb50-4743-8f5a-cfb4eb08344d",
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
    ),
    TrackingLog(
        trackingId = "f66c288c-eb50-4743-8f5a-cfb4eb08344d",
        state = TrackingState.APPROVED,
        message = "Reservation for vehicle has been created",
        assignedBy = User(
            id = "f66c288c-eb50-4743-8f5a-cfb4eb08344d",
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
    ),
    TrackingLog(
        trackingId = "f66c288c-eb50-4743-8f5a-cfb4eb08344d",
        state = TrackingState.ACTIVE,
        message = "Reservation for vehicle has been created",
        assignedBy = User(
            id = "f66c288c-eb50-4743-8f5a-cfb4eb08344d",
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
    ),
    TrackingLog(
        trackingId = "f66c288c-eb50-4743-8f5a-cfb4eb08344d",
        state = TrackingState.RETURNED,
        message = "Reservation for vehicle has been created",
        assignedBy = User(
            id = "f66c288c-eb50-4743-8f5a-cfb4eb08344d",
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
    ),
//    TrackingLog(
//        trackingId = "f66c288c-eb50-4743-8f5a-cfb4eb08344d",
//        state = TrackingState.COMPLETED,
//        message = "Reservation for vehicle has been created",
//        assignedBy = User(
//            firstName = "John",
//            lastName = "Doe",
//            fullName = "John Doe",
//            email = "john.doe@seznam.cz",
//            phoneNumber = "+420123456789",
//            role = Role.ADMIN,
//            maximumDistance = 100,
//            overallDistance = 0
//        ),
//        assignedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
//        images = emptyList(),
//    )
)

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