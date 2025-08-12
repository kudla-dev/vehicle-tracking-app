package cz.kudladev.vehicletracking.core.ui.calendar

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDirection
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.until
import org.jetbrains.compose.resources.stringResource
import vehicletracking.core.ui.generated.resources.Res
import vehicletracking.core.ui.generated.resources.april
import vehicletracking.core.ui.generated.resources.august
import vehicletracking.core.ui.generated.resources.december
import vehicletracking.core.ui.generated.resources.february
import vehicletracking.core.ui.generated.resources.friday
import vehicletracking.core.ui.generated.resources.january
import vehicletracking.core.ui.generated.resources.july
import vehicletracking.core.ui.generated.resources.june
import vehicletracking.core.ui.generated.resources.march
import vehicletracking.core.ui.generated.resources.may
import vehicletracking.core.ui.generated.resources.monday
import vehicletracking.core.ui.generated.resources.november
import vehicletracking.core.ui.generated.resources.october
import vehicletracking.core.ui.generated.resources.saturday
import vehicletracking.core.ui.generated.resources.september
import vehicletracking.core.ui.generated.resources.sunday
import vehicletracking.core.ui.generated.resources.thursday
import vehicletracking.core.ui.generated.resources.tuesday
import vehicletracking.core.ui.generated.resources.wednesday

object DateTimeOperations {

    private var dateTimePickerDefaults = DateTimePickerDefaults()

    fun setDateTimePickerDefaults(dateTimePickerDefaults: DateTimePickerDefaults) {
        this.dateTimePickerDefaults = dateTimePickerDefaults
    }

    fun getLocalDate(): LocalDate {
        return Clock.System.now().toLocalDateTime(dateTimePickerDefaults.timeZone).date
    }

    fun LocalDate.addMonth(): LocalDate {
        return this.plus(1, DateTimeUnit.MONTH)
    }

    fun LocalDate.subtractMonth(): LocalDate {
        return this.minus(1, DateTimeUnit.MONTH)
    }

    fun LocalDate.getMonthName(): String {
        return dateTimePickerDefaults.monthNames.names[this.month.ordinal] + " " + this.year
    }

    private fun getNumberOfDaysInMonth(localDate: LocalDate): Int {
        val start = LocalDate(localDate.year, localDate.month, 1)
        val end = start.plus(1, DateTimeUnit.MONTH)
        return start.until(end, DateTimeUnit.DAY)
    }

    fun getCalendarDates(localDate: LocalDate): List<CalendarDate> {
        val firstDayOfTheMonth = LocalDate(localDate.year, localDate.month, 1)
        val numberOfDaysInMonth = getNumberOfDaysInMonth(localDate)
        val today = getLocalDate()
        val time = Clock.System.now().toLocalDateTime(dateTimePickerDefaults.timeZone).time
        val selectedMonth = localDate.month
        val selectedYear = localDate.year

        val calendarDates = mutableListOf<CalendarDate>()

        val firstDayOfWeek = firstDayOfTheMonth.dayOfWeek.ordinal
        val daysFromPreviousMonth = if (firstDayOfWeek == 0) 0 else firstDayOfWeek

        val previousMonth = localDate.minus(1, DateTimeUnit.MONTH)
        val numberOfDaysInPreviousMonth = getNumberOfDaysInMonth(previousMonth)
        for (i in daysFromPreviousMonth downTo 1) {
            val date = LocalDate(previousMonth.year, previousMonth.month, numberOfDaysInPreviousMonth - i + 1)
            calendarDates.add(
                CalendarDate(
                    date,
                    date.dayOfWeek,
                    date.dayOfMonth,
                    false,
                    date == today,
                    false,
                    isDisabled =
                        (date in dateTimePickerDefaults.disabledDatesWithTimeSlot.keys && (dateTimePickerDefaults.disabledDatesWithTimeSlot[date]?.size == 0)) || (dateTimePickerDefaults.disablePastDates && date < today) || (date == today && time > LocalTime(20, 0)),
                    availableTimeSlots = createTimeSlot(dateTimePickerDefaults.disabledDatesWithTimeSlot[date])
                )
            )
        }

        for (day in 1..numberOfDaysInMonth) {
            val date = LocalDate(selectedYear, selectedMonth, day)
            calendarDates.add(
                CalendarDate(
                    date,
                    date.dayOfWeek,
                    day,
                    true,
                    date == today,
                    false,
                    isDisabled = (date in dateTimePickerDefaults.disabledDatesWithTimeSlot.keys && (dateTimePickerDefaults.disabledDatesWithTimeSlot[date]?.size == 0))  || (dateTimePickerDefaults.disablePastDates && date < today) || (date == today && time > LocalTime(20, 0)),
                    availableTimeSlots = createTimeSlot(dateTimePickerDefaults.disabledDatesWithTimeSlot[date])
                )
            )
        }

        val daysFromNextMonth = (7 - (calendarDates.size % 7)) % 7
        val nextMonth = localDate.plus(1, DateTimeUnit.MONTH)
        for (day in 1..daysFromNextMonth) {
            val date = LocalDate(nextMonth.year, nextMonth.month, day)
            calendarDates.add(
                CalendarDate(
                    date,
                    date.dayOfWeek,
                    day,
                    false,
                    date == today,
                    false,
                    isDisabled = (date in dateTimePickerDefaults.disabledDatesWithTimeSlot.keys && (dateTimePickerDefaults.disabledDatesWithTimeSlot[date]?.size == 0)) || (dateTimePickerDefaults.disablePastDates && date < today) || (date == today && time > LocalTime(20, 0)),
                    availableTimeSlots = createTimeSlot(dateTimePickerDefaults.disabledDatesWithTimeSlot[date])
                )
            )
        }
        return calendarDates
    }

    private fun createTimeSlot(availableTimes: List<LocalTime>?): List<TimeSlot>{
        // 8:00 - 20:00
        val timeSlots = mutableListOf<TimeSlot>()
        for (hour in 8..19){
            timeSlots.add(
                TimeSlot(
                    time = LocalTime(hour, 0).toString(),
                    isAvailable = availableTimes?.contains(LocalTime(hour, 0)) ?: true,
                    canStartRange = availableTimes?.contains(LocalTime(hour,30)) ?: true && availableTimes?.contains(LocalTime(hour,0)) ?: true
                )
            )
            timeSlots.add(
                TimeSlot(
                    time = LocalTime(hour, 30).toString(),
                    isAvailable = availableTimes?.contains(LocalTime(hour, 30)) ?: true,
                    canStartRange = availableTimes?.contains(LocalTime(hour,30)) ?: true && availableTimes?.contains(LocalTime(hour + 1,0)) ?: true
                )
            )
        }
        timeSlots.add(TimeSlot(LocalTime(20, 0).toString(), availableTimes?.contains(LocalTime(20, 0)) ?: true, canStartRange = false))

        return timeSlots
    }

    fun checkAvailableTimeSlotsForDay(calendarDates: List<CalendarDate>, selectedDate: LocalDate, selectedTime: LocalTime): Boolean {
        val selectedDateIndex = calendarDates.indexOfFirst { it.date == selectedDate }
        val selectedDateObject = calendarDates[selectedDateIndex]
        val selectedTimeIndex = selectedDateObject.availableTimeSlots.indexOfFirst { it.time == selectedTime.toString() }
        selectedDateObject.availableTimeSlots.forEachIndexed { index, timeSlot ->
            if (index > selectedTimeIndex && !timeSlot.isAvailable) {
                return false
            }
        }
        if (selectedDateIndex == calendarDates.size - 1) {
            return true
        } else {
            val nextDay = calendarDates[selectedDateIndex + 1]
            return nextDay.availableTimeSlots.first().isAvailable
        }
    }

    fun updateAvailableTimesAndDisabledDates(_calendarDates: List<CalendarDate>, selectedDate: LocalDate, selectedTime: LocalTime): List<CalendarDate> {
        val calendarDates = _calendarDates
        // Go from the selected date to selected time and update available slots, so that if there is a slot that is not available all rest slots after that will be also unavailable and disabled, so that will be no overlapping
        var selectedDateIndex = calendarDates.indexOfFirst { it.date == selectedDate }

        if (selectedDateIndex == - 1){
            selectedDateIndex = 0
        }

        val selectedDateObject = calendarDates[selectedDateIndex]
        val selectedTimeIndex = selectedDateObject.availableTimeSlots.indexOfFirst { it.time == selectedTime.toString() }
        var foundFirstDisabled = false
        selectedDateObject.availableTimeSlots = selectedDateObject.availableTimeSlots.mapIndexed { index, timeSlot ->
            if (index <= selectedTimeIndex) {
                timeSlot.copy(
                    isAvailable = false
                )
            } else {
                if (timeSlot.isAvailable && !foundFirstDisabled) {
                    timeSlot.copy(
                        isAvailable = true
                    )
                } else {
                    foundFirstDisabled = true
                    timeSlot.copy(
                        isAvailable = false
                    )
                }
            }
        }
        for (i in selectedDateIndex + 1 until calendarDates.size) {
            val dateObject = calendarDates[i]
            dateObject.availableTimeSlots = dateObject.availableTimeSlots.map { timeSlot ->
                if (timeSlot.isAvailable && !foundFirstDisabled) {
                    timeSlot
                } else {
                    foundFirstDisabled = true
                    timeSlot.copy(
                        isAvailable = false
                    )
                }
            }
        }


        println("calendarDates: ${calendarDates[selectedDateIndex]}")
        return calendarDates
    }
}

data class CalendarDate(
    val date: LocalDate,
    val dayOfWeek: DayOfWeek,
    val day: Int,
    val isCurrentMonth: Boolean,
    val isToday: Boolean,
    var isSelected: Boolean,
    var isInSelectedRange: Boolean = false,
    var isStartOfRange: Boolean = false,
    var isDisabled: Boolean = false,
    var availableTimeSlots: List<TimeSlot> = emptyList()
)

data class TimeSlot(
    val time: String, // Example: "08:00", "08:30"
    var isAvailable: Boolean = false,
    var canStartRange: Boolean = false,
)

data class DateTimePickerDefaults(
    val monthNames: MonthNames = MonthNames.ENGLISH_FULL,
    val dayOfWeekNames: DayOfWeekNames = DayOfWeekNames.ENGLISH_FULL,
    val timeZone: TimeZone = TimeZone.currentSystemDefault(),
    var dayOfWeekNamesShort: DayOfWeekNames = DayOfWeekNames.ENGLISH_ABBREVIATED,
    var disabledDatesWithTimeSlot : Map<LocalDate, List<LocalTime>> = emptyMap(),
    var disablePastDates: Boolean = false,
    val formater: DateTimeFormat<LocalDate> = LocalDate.Format {
        dayOfWeek(dayOfWeekNamesShort)
        chars(", ")
        date(LocalDate.Format {
            monthName(MonthNames.ENGLISH_FULL)
            chars(" ")
            dayOfMonth()
        })
    }
){
    init {
        val listOfDays = mutableListOf<String>()
        for (day in dayOfWeekNames.names.indices){
            listOfDays.add(dayOfWeekNames.names[day].substring(0, 3))
        }
        dayOfWeekNamesShort = DayOfWeekNames(
            monday = listOfDays[0],
            tuesday = listOfDays[1],
            wednesday = listOfDays[2],
            thursday = listOfDays[3],
            friday = listOfDays[4],
            saturday = listOfDays[5],
            sunday = listOfDays[6]
        )
    }
    companion object {
        @Composable
        fun localizedMonthNames(): MonthNames {
            return MonthNames(
                january = stringResource(Res.string.january),
                february = stringResource(Res.string.february),
                march = stringResource(Res.string.march),
                april = stringResource(Res.string.april),
                may = stringResource(Res.string.may),
                june = stringResource(Res.string.june),
                july = stringResource(Res.string.july),
                august = stringResource(Res.string.august),
                september = stringResource(Res.string.september),
                october = stringResource(Res.string.october),
                november = stringResource(Res.string.november),
                december = stringResource(Res.string.december)
            )
        }

        @Composable
        fun localizedDayOfWeekNames(): DayOfWeekNames {
            return DayOfWeekNames(
                monday = stringResource(Res.string.monday),
                tuesday = stringResource(Res.string.tuesday),
                wednesday = stringResource(Res.string.wednesday),
                thursday = stringResource(Res.string.thursday),
                friday = stringResource(Res.string.friday),
                saturday = stringResource(Res.string.saturday),
                sunday = stringResource(Res.string.sunday)
            )
        }
    }
}

data class DateTimePickerColors(
    val selectedDateColor: Color,
    val disabledDateColor: Color,
    val todayDateBorderColor: Color,
    val rangeDateDateColor: Color,
    val textDisabledDateColor: Color,
    val textSelectedDateColor: Color,
    val textTodayDateColor: Color,
    val textCurrentMonthDateColor: Color,
    val textOtherColor: Color,
)