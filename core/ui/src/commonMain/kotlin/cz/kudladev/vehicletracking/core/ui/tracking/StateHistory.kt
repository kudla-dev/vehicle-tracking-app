package cz.kudladev.vehicletracking.core.ui.tracking

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pushpal.jetlime.EventPointAnimation
import com.pushpal.jetlime.EventPointType
import com.pushpal.jetlime.EventPosition
import com.pushpal.jetlime.ItemsList
import com.pushpal.jetlime.JetLimeColumn
import com.pushpal.jetlime.JetLimeDefaults
import com.pushpal.jetlime.JetLimeEventDefaults
import com.pushpal.jetlime.JetLimeExtendedEvent
import cz.kudladev.vehicletracking.core.designsystem.theme.AppTheme
import cz.kudladev.vehicletracking.core.ui.util.toFormattedString
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
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "State History",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        temp.forEachIndexed { index, log ->
            StateHistoryItem(
                trackingLog = log,
                number = index + 1,
                isLast = index == temp.lastIndex,
            )
        }
    }
}

internal val items = listOf(
    TrackingLog(
        trackingId = "f66c288c-eb50-4743-8f5a-cfb4eb08344d",
        state = TrackingState.PENDING,
        message = "Reservation for vehicle has been created",
        assignedBy = User(
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
                logs = items
            )
        }
    }
}