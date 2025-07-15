package cz.kudladev.vehicletracking.core.ui.tracking

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Composable
fun TimeRemaining(
    startTime: LocalDateTime,
    endTime: LocalDateTime,
    modifier: Modifier = Modifier
) {
    var currentInstant by remember { mutableStateOf(Clock.System.now()) }

    // Update every second
    LaunchedEffect(Unit) {
        while(true) {
            currentInstant = Clock.System.now()
            delay(1000)
        }
    }

    val timeZone = TimeZone.currentSystemDefault()
    val startInstant = startTime.toInstant(timeZone)
    val endInstant = endTime.toInstant(timeZone)

    val progressPercentage = calculateProgressPercentage(startInstant, endInstant, currentInstant)
    val remainingTimeText = formatRemainingTime(startInstant, endInstant, currentInstant)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Time Remaining",
            style = MaterialTheme.typography.titleSmall,
        )
        LinearProgressIndicator(
            progress = { progressPercentage / 100f },
            modifier = Modifier.fillMaxWidth(),
            color = ProgressIndicatorDefaults.linearColor,
            trackColor = ProgressIndicatorDefaults.linearTrackColor,
            strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
        )
        Text(
            text = remainingTimeText,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private fun calculateProgressPercentage(
    startInstant: Instant,
    endInstant: Instant,
    currentInstant: Instant
): Float {
    // Handle invalid time ranges
    if (endInstant <= startInstant) {
        return 0f
    }

    // If not started yet
    if (currentInstant < startInstant) {
        return 0f
    }

    // If already finished
    if (currentInstant >= endInstant) {
        return 100f
    }

    val totalDuration = endInstant.minus(startInstant)
    val elapsedDuration = currentInstant.minus(startInstant)

    return (elapsedDuration.inWholeSeconds.toFloat() / totalDuration.inWholeSeconds)
        .coerceIn(0f, 1f) * 100f
}

private fun formatRemainingTime(
    startInstant: Instant,
    endInstant: Instant,
    currentInstant: Instant
): String {
    if (endInstant <= startInstant) {
        return "Invalid time range"
    }

    if (currentInstant < startInstant) {
        return "Not started yet"
    }

    if (currentInstant >= endInstant) {
        return "Ended"
    }

    val remainingDuration = endInstant.minus(currentInstant)
    return formatDuration(remainingDuration)
}

private fun formatDuration(duration: Duration): String {
    val totalSeconds = duration.inWholeSeconds
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return buildString {
        if (hours > 0) append("$hours hours, ")
        if (hours > 0 || minutes > 0) append("$minutes minutes, ")
        append("$seconds seconds")
    }
}