package cz.kudladev.vehicletracking.core.ui.util

import kotlinx.datetime.LocalDateTime

fun LocalDateTime.toFormattedLongString(): String {
    return "${this.date.dayOfMonth.toString().padStart(2, padChar = '0')}.${this.date.monthNumber.toString().padStart(2, padChar = '0')}.${this.date.year} ${this.hour}:${this.minute.toString().padStart(2, '0')}"
}

fun LocalDateTime.toFormattedShortString(): String {
    return "${this.date.dayOfMonth.toString().padStart(2, padChar = '0')}.${this.date.monthNumber.toString().padStart(2, padChar = '0')} ${this.hour}:${this.minute.toString().padStart(2, '0')}"
}

