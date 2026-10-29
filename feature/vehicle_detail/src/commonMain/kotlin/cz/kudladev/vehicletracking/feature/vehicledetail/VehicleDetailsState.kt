package cz.kudladev.vehicletracking.feature.vehicledetail

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute

/** Widen the legacy argument before Navigation reads it with the Long NavType. */
internal fun SavedStateHandle.vehicleDetails(): VehicleDetails {
    val legacyId = get<Any?>("vehicleId")
    if (legacyId is Int) this["vehicleId"] = legacyId.toLong()
    return toRoute<VehicleDetails>()
}
