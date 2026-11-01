package cz.kudladev.vehicletracking.mock

import cz.kudladev.vehicletracking.network.*
import cz.kudladev.vehicletracking.core.data.tracking.models.TrackingResponse
import cz.kudladev.vehicletracking.core.data.vehicles.models.VehicleBasic
import kotlinx.serialization.json.*
import java.io.File
import java.nio.file.Files
import kotlin.test.*

class MockSandboxTest {
    @Test fun resetArchivesOnlyMockSessionAndWholeUploadJournalWithEvidence() {
        val root = Files.createTempDirectory("mock-reset").toFile()
        try {
            val mock = AppConfig.create(AppEnvironment.MOCK)
            val dev = AppConfig.create(AppEnvironment.DEV,"http://localhost:8080")
            fun seed(config: AppConfig) = File(root,config.storageScope.directory).also { d ->
                File(d,"image-uploads").mkdirs()
                File(d,"vehicle_tracking.preferences_pb").writeText("session")
                File(d,"image-uploads/job.json").writeText("pending")
                File(d,"image-uploads/job.jpg").writeBytes(byteArrayOf(1,2,3))
            }
            val mockDir=seed(mock);val devDir=seed(dev)
            val hosted = listOf(AppEnvironment.STAGE, AppEnvironment.PROD).map {
                seed(AppConfig.create(it,"https://${it.name.lowercase()}.example.invalid"))
            }
            val legacy=File(root,"legacy-photo.jpg").also { it.writeBytes(byteArrayOf(9)) }
            archiveMockSandbox(dev,root)
            assertTrue(devDir.exists())
            archiveMockSandbox(mock,root)
            assertFalse(mockDir.exists())
            val archive=root.listFiles()!!.single { it.name.startsWith("mock-archive-") }
            assertEquals("session",File(archive,"vehicle_tracking.preferences_pb").readText())
            assertEquals("pending",File(archive,"image-uploads/job.json").readText())
            assertContentEquals(byteArrayOf(1,2,3),File(archive,"image-uploads/job.jpg").readBytes())
            assertEquals("session",File(devDir,"vehicle_tracking.preferences_pb").readText())
            hosted.forEach { assertContentEquals(byteArrayOf(1,2,3),File(it,"image-uploads/job.jpg").readBytes()) }
            assertContentEquals(byteArrayOf(9),legacy.readBytes())
            archiveMockSandbox(mock,root) // absent sandbox is a repeatable no-op
            assertEquals(1,root.listFiles()!!.count { it.name.startsWith("mock-archive-") })
        } finally { root.deleteRecursively() }
    }
    @Test fun bothSanitizedFixtureSnapshotsStillDecodeWithActualClientContract() {
        val json=Json { ignoreUnknownKeys=true }
        for(name in listOf("client","backend")) {
            val fixture=json.parseToJsonElement(javaClass.getResource("/contract/$name.json")!!.readText()).jsonObject
            val vehicle=json.decodeFromJsonElement<VehicleBasic>(fixture["vehicle"]!!)
            val tracking=json.decodeFromJsonElement<TrackingResponse>(fixture["tracking"]!!)
            assertEquals(MockServer.VEHICLE.toLong(),vehicle.id)
            assertEquals(vehicle,tracking.vehicle)
            assertEquals(MockServer.USER,tracking.user.id)
        }
    }
}
