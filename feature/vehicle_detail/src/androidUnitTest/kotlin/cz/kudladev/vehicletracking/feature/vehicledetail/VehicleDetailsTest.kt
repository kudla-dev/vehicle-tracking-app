package cz.kudladev.vehicletracking.feature.vehicledetail

import androidx.lifecycle.SavedStateHandle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@org.junit.runner.RunWith(org.robolectric.RobolectricTestRunner::class)
@org.robolectric.annotation.Config(manifest = org.robolectric.annotation.Config.NONE, sdk = [35])
class VehicleDetailsTest {
    @Test fun savedArgumentsPreserveLongBoundaries() {
        for (id in listOf(7L, Int.MAX_VALUE.toLong(), 2147483648L, Long.MAX_VALUE)) {
            val original = SavedStateHandle(mapOf("vehicleId" to id))
            val parcel = android.os.Parcel.obtain()
            val restored = try {
                parcel.writeBundle(original.savedStateProvider().saveState())
                parcel.setDataPosition(0)
                SavedStateHandle.createHandle(parcel.readBundle(javaClass.classLoader), null)
            } finally { parcel.recycle() }
            assertEquals(id, restored.vehicleDetails().vehicleId)
        }
    }

    @Test fun missingAndNullArgumentsRemainNull() {
        assertNull(SavedStateHandle().vehicleDetails().vehicleId)
        assertNull(SavedStateHandle(mapOf("vehicleId" to null)).vehicleDetails().vehicleId)
    }

    @Test fun legacyIntArgumentCanBeRead() {
        for (id in listOf(7, Int.MAX_VALUE)) {
            val handle = SavedStateHandle(mapOf("vehicleId" to id))
            assertEquals(id.toLong(), handle.vehicleDetails().vehicleId)
            assertEquals(id.toLong(), handle.get<Long>("vehicleId"))
        }
    }
}
