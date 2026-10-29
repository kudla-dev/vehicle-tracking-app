package cz.kudladev.vehicletracking.core.data.contract

import cz.kudladev.vehicletracking.core.data.tracking.models.TrackingLogDTO
import cz.kudladev.vehicletracking.core.data.tracking.models.toDomain
import kotlinx.serialization.json.Json
import kotlin.test.*

class NullableTrackingLogTest {
    @Test fun absentAndNullActorTimestampAndPhotosAreNotInvented() {
        for (json in listOf("""{"trackingId":"fixture","state":"pending"}""",
            """{"trackingId":"fixture","state":"pending","assignedBy":null,"assignedAt":null,"images":null}""")) {
            val log = Json.decodeFromString<TrackingLogDTO>(json).toDomain()
            assertNull(log.assignedBy)
            assertNull(log.assignedAt)
            assertEquals(emptyList(), log.images)
        }
    }
}
