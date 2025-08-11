package cz.kudladev.vehicletracking.core.data.vehicles.models

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone

fun getTimezoneId(): String {
    return TimeZone.currentSystemDefault().id
}

fun Map<String, List<String>>.toCalendarMap(): Map<LocalDate, List<LocalTime>> {
    return this.mapKeys { LocalDate.parse(it.key) }
        .mapValues { entry ->
            entry.value.map { LocalTime.parse(it) }
        }
}