package cz.kudladev.vehicletracking.core.ui.util

import kotlinx.datetime.LocalDateTime

fun LocalDateTime.toFormattedString(): String {
    return "${this.date.dayOfMonth}.${this.date.monthNumber.toString().padStart(2, padChar = '0')}.${this.date.year} ${this.hour}:${this.minute.toString().padStart(2, '0')}"
}