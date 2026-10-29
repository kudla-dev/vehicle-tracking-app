package cz.kudladev.vehicletracking.core.data.images

import cz.kudladev.vehicletracking.core.data.images.upload.*
import cz.kudladev.vehicletracking.core.domain.vehicles.ProgressUpdate
import cz.kudladev.vehicletracking.model.ImageWithUrl
import cz.kudladev.vehicletracking.model.Result
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class PersistentUploadQueueTest {
    private val photo = ImageWithUrl("photo", "http://localhost/images/photo", 0)
    private fun record(id: String = "job") = UploadRecord(id, "tag", vehicleId = 7, position = 0)

    @Test fun progressOnlyDoesNotDeleteEvidence() = runTest {
        val store = MemoryStore()
        val queue = PersistentUploadQueue(store, this) { _, _ -> flowOf(Result.Success(ProgressUpdate(10, 10))) }
        queue.enqueue(record(), byteArrayOf(1, 2))
        advanceUntilIdle()
        assertEquals(UploadRecord.State.FAILED, queue.snapshots.value.single().record.state)
        assertContentEquals(byteArrayOf(1, 2), store.bytes["job"])
    }

    @Test fun successPersistsAcknowledgementBeforeDeletingBytes() = runTest {
        val store = MemoryStore()
        val queue = PersistentUploadQueue(store, this) { _, _ -> flowOf(Result.Success(ProgressUpdate(10, 10, photo))) }
        queue.enqueue(record(), byteArrayOf(1))
        advanceUntilIdle()
        assertEquals(UploadRecord.State.COMPLETED, store.records["job"]?.state)
        assertNull(store.bytes["job"])
        assertEquals("save:COMPLETED", store.events[store.events.indexOf("delete:job") - 1])
    }

    @Test fun serverFailureRetainsEvidence() = runTest {
        val store = MemoryStore()
        val queue = PersistentUploadQueue(store, this) { _, _ -> flowOf(Result.Error(uploadError(500, "failed"))) }
        queue.enqueue(record(), byteArrayOf(1))
        advanceUntilIdle()
        assertEquals(UploadRecord.State.FAILED, store.records["job"]?.state)
        assertNotNull(store.bytes["job"])
    }

    @Test fun interruptedUploadRestoresAsFailureWithoutPostingAgain() = runTest {
        val store = MemoryStore()
        store.save(record().copy(state = UploadRecord.State.UPLOADING))
        store.saveBytes("job", byteArrayOf(1))
        var calls = 0
        val queue = PersistentUploadQueue(store, this) { _, _ -> calls++; flowOf() }
        advanceUntilIdle()
        assertEquals(0, calls)
        assertEquals(UploadRecord.State.FAILED, queue.snapshots.value.single().record.state)
        assertNotNull(store.bytes["job"])
    }

    @Test fun cancellationAndClearRetainActiveEvidence() = runTest {
        val store = MemoryStore()
        val wait = CompletableDeferred<Unit>()
        val queue = PersistentUploadQueue(store, this) { _, _ -> flow { wait.await() } }
        queue.enqueue(record(), byteArrayOf(1))
        queue.enqueue(record("queued"), byteArrayOf(2))
        runCurrent()
        queue.cancel("job")
        queue.clear()
        advanceUntilIdle()
        assertEquals(UploadRecord.State.CANCELED, store.records["job"]?.state)
        assertEquals(UploadRecord.State.CANCELED, store.records["queued"]?.state)
        assertNotNull(store.bytes["job"])
        assertNotNull(store.bytes["queued"])
    }

    @Test fun diskFailurePreventsNetwork() = runTest {
        val store = MemoryStore().apply { failWrites = true }
        var calls = 0
        val queue = PersistentUploadQueue(store, this) { _, _ -> calls++; flowOf() }
        assertFailsWith<IllegalStateException> { queue.enqueue(record(), byteArrayOf(1)) }
        advanceUntilIdle()
        assertEquals(0, calls)
    }

    @Test fun failureToPersistAcknowledgementRetainsBytes() = runTest {
        val store = MemoryStore()
        val queue = PersistentUploadQueue(store, this) { _, _ -> flow {
            store.failWrites = true
            emit(Result.Success(ProgressUpdate(1, 1, photo)))
        } }
        queue.enqueue(record(), byteArrayOf(1))
        advanceUntilIdle()
        assertEquals(UploadRecord.State.FAILED, queue.snapshots.value.single().record.state)
        assertNotNull(store.bytes["job"])
    }

    private class MemoryStore : UploadStore {
        val records = mutableMapOf<String, UploadRecord>()
        val bytes = mutableMapOf<String, ByteArray>()
        val events = mutableListOf<String>()
        var failWrites = false
        override fun load() = records.values.toList()
        override fun save(record: UploadRecord) { check(!failWrites); records[record.id] = record; events += "save:${record.state}" }
        override fun saveBytes(id: String, bytes: ByteArray) { check(!failWrites); this.bytes[id] = bytes }
        override fun readBytes(id: String) = requireNotNull(bytes[id])
        override fun deleteBytes(id: String) { bytes.remove(id); events += "delete:$id" }
        override fun deleteRecord(id: String) { records.remove(id) }
    }
}
