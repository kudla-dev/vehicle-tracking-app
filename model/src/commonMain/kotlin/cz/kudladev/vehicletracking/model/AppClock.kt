package cz.kudladev.vehicletracking.model

import kotlinx.datetime.Clock

/** Host-owned clock; production hosts retain the system clock. */
object AppClock {
    var clock: Clock = Clock.System
    fun now() = clock.now()
}
