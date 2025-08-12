package cz.kudladev.vehicletracking.core.ui.calendar

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.valentinilk.shimmer.shimmer
import cz.kudladev.vehicletracking.core.designsystem.theme.AppTheme
import cz.kudladev.vehicletracking.core.ui.calendar.DateTimeOperations.addMonth
import cz.kudladev.vehicletracking.core.ui.calendar.DateTimeOperations.checkAvailableTimeSlotsForDay
import cz.kudladev.vehicletracking.core.ui.calendar.DateTimeOperations.getCalendarDates
import cz.kudladev.vehicletracking.core.ui.calendar.DateTimeOperations.getLocalDate
import cz.kudladev.vehicletracking.core.ui.calendar.DateTimeOperations.getMonthName
import cz.kudladev.vehicletracking.core.ui.calendar.DateTimeOperations.subtractMonth
import cz.kudladev.vehicletracking.core.ui.calendar.DateTimeOperations.updateAvailableTimesAndDisabledDates
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import vehicletracking.core.ui.generated.resources.*

@Composable
fun DatePickerWithTimePicker(
    modifier: Modifier = Modifier,
    range: Boolean = false,
    onSelectDate: (LocalDate) -> Unit = {},
    onRangeSelected: (LocalDateTime?, LocalDateTime?) -> Unit = { _, _ -> },
    clickable: Boolean = true,
    dateTimePickerDefaults: DateTimePickerDefaults = DateTimePickerDefaults(
        monthNames = DateTimePickerDefaults.localizedMonthNames(),
        dayOfWeekNames = DateTimePickerDefaults.localizedDayOfWeekNames()
    ),
    dateTimePickerColors: DateTimePickerColors = DateTimePickerColors(
        selectedDateColor = MaterialTheme.colorScheme.primary,
        disabledDateColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
        todayDateBorderColor = MaterialTheme.colorScheme.primary,
        rangeDateDateColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
        textDisabledDateColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
        textSelectedDateColor = MaterialTheme.colorScheme.onPrimary,
        textTodayDateColor = MaterialTheme.colorScheme.primary,
        textCurrentMonthDateColor = MaterialTheme.colorScheme.onSurface,
        textOtherColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
    )
) {
    DateTimeOperations.setDateTimePickerDefaults(dateTimePickerDefaults)

    var selectedMonth by remember { mutableStateOf(getLocalDate()) }

    var firstDate by remember { mutableStateOf<CalendarDate?>(null) }
    var firstTime by remember { mutableStateOf<LocalTime?>(null) }
    var showFirstTimeSelect by remember { mutableStateOf(false) }

    var secondDate by remember { mutableStateOf<CalendarDate?>(null) }
    var secondTime by remember { mutableStateOf<LocalTime?>(null) }
    var showSecondTimeSelect by remember { mutableStateOf(false) }


    var formatedDayFrom by remember { mutableStateOf("") }
    var formatedDayTo by remember { mutableStateOf("") }
    var finalDate by remember { mutableStateOf<String?>(null) }

    val calendarDates by remember(selectedMonth, firstDate, firstTime, secondDate, secondTime) {
        derivedStateOf {
            println("Triggered")
            var dates = getCalendarDates(selectedMonth)
            if (firstDate != null && secondDate != null && range) {
                val startDate = firstDate!!
                val endDate = secondDate!!
                val start = if (startDate.date < endDate.date) startDate else endDate
                val end = if (startDate.date < endDate.date) endDate else startDate
                val rangeHasDisabledDate = dates.any { it.date in start.date..end.date && it.isDisabled }
                if (rangeHasDisabledDate) {
                    firstDate = secondDate
                    secondDate = null
                    formatedDayFrom = firstDate!!.date.format()
                    finalDate = formatedDayFrom
                    onSelectDate(firstDate!!.date)
                    dates.map {
                        it.copy(isSelected = it.date == firstDate!!.date)
                    }
                } else {
                    formatedDayFrom = start.date.format()
                    formatedDayTo = end.date.format()
                    finalDate = "$formatedDayFrom - $formatedDayTo"
                    if (firstTime != null) {
                        dates = updateAvailableTimesAndDisabledDates(dates, start.date, firstTime!!)
                        formatedDayFrom = start.date.toString() + " " + firstTime!!.toString()
                    }
                    if (secondTime != null) {
                        formatedDayTo = end.date.toString() + " " + secondTime!!.toString()
                    }
                    dates.map {
                        it.copy(
                            isInSelectedRange = it.date in start.date..end.date,
                            isSelected = it.date == start.date || it.date == end.date,
                            isStartOfRange = it.date == start.date
                        )
                    }
                }
            } else if (firstDate != null) {
                formatedDayFrom = firstDate!!.date.toString()
                finalDate = formatedDayFrom
                if (!range) {
                    onSelectDate(firstDate!!.date)
                }
                if (firstTime != null) {
                    dates = updateAvailableTimesAndDisabledDates(dates, firstDate!!.date, firstTime!!)
                    formatedDayFrom = firstDate!!.date.toString() + " " + firstTime!!.toString()
                }
                dates.map {
                    it.copy(isSelected = it.date == firstDate!!.date)
                }
            } else {
                dates
            }
        }
    }

    Column(
        modifier = modifier
    ) {
        DatePickerWithTimePickerHeader()
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        selectedMonth = selectedMonth.subtractMonth()
                    }
                ){
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = stringResource(Res.string.previousMonth),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.size(32.dp),
                    )
                }
                Text(
                    text = selectedMonth.getMonthName(),
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    fontWeight = FontWeight.SemiBold,
                    fontStyle = FontStyle.Italic,

                )
                IconButton(
                    onClick = {
                        selectedMonth = selectedMonth.addMonth()
                    }
                ){
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = stringResource(Res.string.nextMonth),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            WeekNamesHeader(dateTimePickerDefaults)
            for (i in calendarDates.indices step 7) {
                Row(
                    modifier = Modifier.padding(vertical = 1.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    for (j in i until i + 7) {
                        val calendarDate = calendarDates[j]
                        val numberOfAvailableTimeSlots = calendarDate.availableTimeSlots.count { it.canStartRange }
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .weight(1f)
                                .then(if (firstDate?.date != secondDate?.date) Modifier.datePickerBoxSelectedRange(calendarDate, dateTimePickerColors) else Modifier)
                                .clip(shape = RoundedCornerShape(100))
                                .datePickerBoxToday(calendarDate, dateTimePickerColors)

                                .datePickerBoxSelected(calendarDate, dateTimePickerColors)
                                .clickable(enabled = !calendarDate.isDisabled && clickable) {
                                    if (firstDate == null) {
                                        firstDate = calendarDate
                                    } else if (secondDate == null && range) {
                                        if (calendarDate.date >= firstDate!!.date && firstTime != null && calendarDates.first { it.date == calendarDate.date }.availableTimeSlots.any { it.isAvailable }) {
                                            secondDate = calendarDate
                                        } else {
                                            firstDate = calendarDate
                                            firstTime = null
                                            secondDate = null
                                            secondTime = null
                                            onRangeSelected(null, null)
                                        }
                                    } else {
                                        firstDate = calendarDate
                                        secondDate = null
                                        firstTime = null
                                        secondTime = null
                                        onRangeSelected(null, null)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = calendarDate.day.toString(),
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                color = when {
                                    calendarDate.isDisabled -> dateTimePickerColors.textDisabledDateColor
                                    calendarDate.isSelected -> dateTimePickerColors.textSelectedDateColor
                                    calendarDate.isToday -> dateTimePickerColors.textTodayDateColor
                                    calendarDate.isCurrentMonth -> dateTimePickerColors.textCurrentMonthDateColor
                                    else -> dateTimePickerColors.textOtherColor
                                },
                                fontStyle = MaterialTheme.typography.bodyMedium.fontStyle,
                                fontWeight = when {
                                    calendarDate.isToday -> FontWeight.Bold
                                    calendarDate.isSelected -> FontWeight.SemiBold
                                    else -> FontWeight.Normal
                                }
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.BottomCenter
                            ){
                                if (numberOfAvailableTimeSlots > 0 && !calendarDate.isDisabled) {
                                    Text(
                                        text = numberOfAvailableTimeSlots.toString(),
                                        textAlign = TextAlign.Center,
                                        maxLines = 1,
                                        color = when {
                                            calendarDate.isDisabled -> dateTimePickerColors.textDisabledDateColor
                                            calendarDate.isSelected -> dateTimePickerColors.textSelectedDateColor
                                            calendarDate.isToday -> dateTimePickerColors.textTodayDateColor
                                            calendarDate.isCurrentMonth -> dateTimePickerColors.textCurrentMonthDateColor
                                            else -> dateTimePickerColors.textOtherColor
                                        },
                                        fontSize = 9.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        Text(
            text = stringResource(Res.string.selectTime),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 16.dp),
            fontWeight = FontWeight.SemiBold,
            fontStyle = FontStyle.Italic,
        )
        SelectTimeSection(
            enabled = firstDate != null && firstTime == null && calendarDates.first { it.date == firstDate!!.date }.availableTimeSlots.any { it.canStartRange },
            title = stringResource(Res.string.startTime),
            targetState = when {
                firstDate == null -> stringResource(Res.string.selectDateFirst)
                firstTime == null -> stringResource(Res.string.selectTimeFirst,firstDate?.date?.format() ?: "")
                else -> "${firstDate?.date?.format()} - ${firstTime!!.format()}"
            },
            selectedDate = firstDate,
            selectedTime = firstTime,
            calendarDates = calendarDates,
            onTimeSelect = {
                showFirstTimeSelect = it
            }
        )
        SelectTimeSection(
            enabled = secondDate != null && secondTime == null && calendarDates.first { it.date == secondDate!!.date }.availableTimeSlots.any { it.isAvailable },
            title = stringResource(Res.string.endTime),
            targetState = when {
                firstTime == null && firstDate == null -> stringResource(Res.string.selectStartTimeFirst)
                secondDate == null -> stringResource(Res.string.selectEndDate)
                secondTime == null -> stringResource(Res.string.selectEndTimeFirst, secondDate?.date?.format() ?: "")
                else -> "${secondDate?.date?.format()} - ${secondTime!!.format()}"
            },
            selectedDate = secondDate,
            selectedTime = secondTime,
            calendarDates = calendarDates,
            onTimeSelect = {
                showSecondTimeSelect = it
            }
        )
    }
    if (showFirstTimeSelect) {
        TimeSelect(
            onDismiss = {
                showFirstTimeSelect = false
            },
            times = calendarDates.first { it.date == firstDate!!.date }.availableTimeSlots.filter { it.canStartRange }.map { LocalTime.parse(it.time) },
            selectedTime = firstTime,
            onTimeSelected = {
                firstTime = it
                secondTime = null
                if (!checkAvailableTimeSlotsForDay(calendarDates, firstDate!!.date, firstTime!!)) {
                    secondDate = firstDate
                }
                onRangeSelected(
                    LocalDateTime(firstDate!!.date, firstTime!!),
                    null
                )
                showFirstTimeSelect = false
            }
        )
    }
    if (showSecondTimeSelect) {
        println("Second date: ${calendarDates.first{it.date == secondDate!!.date}}")
        TimeSelect(
            onDismiss = {
                showSecondTimeSelect = false
            },
            times = calendarDates.first { it.date == secondDate!!.date }.availableTimeSlots.filter { it.isAvailable }.map { LocalTime.parse(it.time) },
            selectedTime = secondTime,
            onTimeSelected = {
                secondTime = it
                onRangeSelected(
                    LocalDateTime(firstDate!!.date, firstTime!!),
                    LocalDateTime(secondDate!!.date, secondTime!!)
                )
                showSecondTimeSelect = false
            }
        )
    }
}

@Composable
private fun SelectTimeSection(
    enabled: Boolean = false,
    title: String,
    targetState: String,
    selectedDate: CalendarDate?,
    selectedTime: LocalTime?,
    calendarDates: List<CalendarDate>,
    onTimeSelect: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable(
                enabled = enabled,
                onClick = {
                    onTimeSelect(true)
                }
            )
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
            )
            AnimatedContent(
                targetState = targetState,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith
                            fadeOut(animationSpec = tween(300))
                },
            ) { text ->
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                )
            }
        }
        Icon(
            imageVector = Icons.Default.ArrowBackIosNew,
            contentDescription = "Select time",
            modifier = Modifier
                .rotate(180f)
        )
    }
}

@Composable
private fun Modifier.datePickerBoxToday(date: CalendarDate, dateTimePickerColors: DateTimePickerColors): Modifier {
    return when (date.isToday) {
        true -> this.border(1.dp, dateTimePickerColors.todayDateBorderColor, shape = RoundedCornerShape(100))
        false -> this
    }
}

@Composable
private fun Modifier.datePickerBoxSelected(date: CalendarDate, dateTimePickerColors: DateTimePickerColors): Modifier {
    return when (date.isSelected) {
        true -> this.background(dateTimePickerColors.selectedDateColor, shape = RoundedCornerShape(100))
        false -> this
    }
}

//@Composable
//private fun Modifier.datePickerBoxDisabled(date: CalendarDate, dateTimePickerColors: DateTimePickerColors): Modifier {
//    return when (date.isDisabled) {
//        true -> this.background(dateTimePickerColors.disabledDateColor, shape = RoundedCornerShape(100))
//        false -> this
//    }
//}

@Composable
private fun Modifier.datePickerBoxSelectedRange(date: CalendarDate, dateTimePickerColors: DateTimePickerColors): Modifier {
    return when (date.isInSelectedRange) {
        true -> {
            if (date.isSelected && date.isStartOfRange) {
                this.background(dateTimePickerColors.rangeDateDateColor, shape = RoundedCornerShape(topStart = 100.dp, bottomStart = 100.dp))
            } else if (date.isSelected) {
                this.background(dateTimePickerColors.rangeDateDateColor, shape = RoundedCornerShape(topEnd = 100.dp, bottomEnd = 100.dp))
            } else {
                this.background(dateTimePickerColors.rangeDateDateColor, shape = RoundedCornerShape(0.dp))
            }
        }
        false -> this
    }
}



private fun LocalTime.format(): String {
    return "${this.hour}:${if (this.minute < 10) "0" else ""}${this.minute}"
}

private fun LocalDate.format(): String {
    return "${this.dayOfMonth}.${this.monthNumber}.${this.year}"
}

@Composable
fun DatePickerWithTimePickerSkeleton(
    modifier: Modifier = Modifier,
    dateTimePickerDefaults: DateTimePickerDefaults = DateTimePickerDefaults(),
){
    val currentMonth = getLocalDate()

    Column(
        modifier = modifier
    ) {
        DatePickerWithTimePickerHeader()
        Row(
            modifier = Modifier
                .shimmer()
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = { /*TODO*/ },
                enabled = false,
            ) {
                Icon(
                    modifier = Modifier.size(32.dp),
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Next month",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                )
            }
            Text(
                text = currentMonth.getMonthName(),
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                fontWeight = FontWeight.SemiBold,
                fontStyle = FontStyle.Italic,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )
            IconButton(
                onClick = { /*TODO*/ },
                enabled = false
            ) {
                Icon(
                    modifier = Modifier.size(32.dp),
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Next month",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                )
            }
        }
        WeekNamesHeader(dateTimePickerDefaults)
        repeat(5){
            Row(
                modifier = Modifier
                    .shimmer()
                    .fillMaxWidth()
            ) {
                repeat(7) {
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .weight(1f)
                            .padding(4.dp)
                            .background(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(100.dp)
                            ),
                    )
                }
            }
        }
        Text(
            text = "Select time",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 16.dp),
            fontWeight = FontWeight.SemiBold,
            fontStyle = FontStyle.Italic,
        )
        SelectTimeSection(
            enabled = false,
            title = "Start time",
            targetState = "Select date first",
            selectedDate = null,
            selectedTime = null,
            calendarDates = emptyList(),
            onTimeSelect = { _ -> }
        )
        SelectTimeSection(
            enabled = false,
            title = "End time",
            targetState = "Select start time first",
            selectedDate = null,
            selectedTime = null,
            calendarDates = emptyList(),
            onTimeSelect = { _ -> }
        )

    }
}

@Composable
private fun WeekNamesHeader(dateTimePickerDefaults: DateTimePickerDefaults) {
    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        for (day in dateTimePickerDefaults.dayOfWeekNamesShort.names) {
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day[0].toString(),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    fontStyle = MaterialTheme.typography.bodyMedium.fontStyle,
                )
            }
        }
    }
}

@Composable
private fun DatePickerWithTimePickerHeader() {
    Text(
        text = stringResource(Res.string.selectDateRange),
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
        fontWeight = FontWeight.SemiBold,
        fontStyle = FontStyle.Italic,
    )
}

@Preview
@Composable
private fun DatePickerPreview() {
    AppTheme {
        Surface {
            DatePickerWithTimePicker(
                range = true,
                onSelectDate = {},
                onRangeSelected = { _, _ -> },
                clickable = true,
                dateTimePickerDefaults = DateTimePickerDefaults(
                    disablePastDates = true
                ),
            )
        }
    }
}

@Preview
@Composable
private fun DatePickerWithTimePickerSkeletonPreview() {
    AppTheme {
        Surface {
            DatePickerWithTimePickerSkeleton(
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}