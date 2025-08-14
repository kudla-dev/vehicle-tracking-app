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
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import vehicletracking.core.ui.generated.resources.Res
import vehicletracking.core.ui.generated.resources.days
import vehicletracking.core.ui.generated.resources.ended
import vehicletracking.core.ui.generated.resources.hours
import vehicletracking.core.ui.generated.resources.invalid_time_range
import vehicletracking.core.ui.generated.resources.minutes
import vehicletracking.core.ui.generated.resources.months
import vehicletracking.core.ui.generated.resources.not_started_yet
import vehicletracking.core.ui.generated.resources.seconds
import vehicletracking.core.ui.generated.resources.timeRemaining
import kotlin.time.Duration

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

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(Res.string.timeRemaining),
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
            text = formatRemainingTime(startInstant, endInstant, currentInstant),
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

@Composable
private fun formatRemainingTime(
    startInstant: Instant,
    endInstant: Instant,
    currentInstant: Instant
): String {
    if (endInstant <= startInstant) {
        return stringResource(Res.string.invalid_time_range)
    }

    if (currentInstant < startInstant) {
        return stringResource(Res.string.not_started_yet)
    }

    if (currentInstant >= endInstant) {
        return stringResource(Res.string.ended)
    }

    val remainingDuration = endInstant.minus(currentInstant)
    return formatDuration(remainingDuration)
}

@Composable
private fun formatDuration(duration: Duration): String {
    val totalSeconds = duration.inWholeSeconds

    // Calculate time units
    val secondsInMonth = 30 * 24 * 3600L
    val secondsInDay = 24 * 3600L

    val months = totalSeconds / secondsInMonth
    val days = (totalSeconds % secondsInMonth) / secondsInDay
    val hours = (totalSeconds % secondsInDay) / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    // Create parts list and filter out zero values
    val parts = mutableListOf<String>()

    if (months > 0) {
        parts.add(pluralStringResource(Res.plurals.months, months.toInt(), months.toInt()))
    }

    if (days > 0) {
        parts.add(pluralStringResource(Res.plurals.days, days.toInt(), days.toInt()))
    }

    if (hours > 0) {
        parts.add(pluralStringResource(Res.plurals.hours, hours.toInt(), hours.toInt()))
    }

    if (minutes > 0) {
        parts.add(pluralStringResource(Res.plurals.minutes, minutes.toInt(), minutes.toInt()))
    }

    if (seconds > 0 || parts.isEmpty()) {
        parts.add(pluralStringResource(Res.plurals.seconds, seconds.toInt(), seconds.toInt()))
    }

    // Take only the most significant parts based on duration magnitude
    val significantParts = parts.take(minOf(parts.size, if (months > 0) 2 else 3))

    return significantParts.joinToString(", ")
}