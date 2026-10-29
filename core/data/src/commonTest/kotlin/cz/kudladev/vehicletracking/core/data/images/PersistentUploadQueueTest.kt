package cz.kudladev.vehicletracking.core.data.images

import cz.kudladev.vehicletracking.core.data.images.upload.*
import cz.kudladev.vehicletracking.core.domain.vehicles.ProgressUpdate
import cz.kudladev.vehicletracking.model.ImageWithUrl
import cz.kudladev.vehicletracking.model.Result
import kotlinx.coroutines.launch
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

    @Test fun duplicateIdCannotOverwriteEvidenceOrPostTwice() = runTest {
        val store = MemoryStore()
        var calls = 0
        val queue = PersistentUploadQueue(store, this) { _, _ -> calls++; flowOf(Result.Error(uploadError(500, "failed"))) }
        queue.enqueue(record(), byteArrayOf(1))
        assertFailsWith<IllegalArgumentException> { queue.enqueue(record(), byteArrayOf(2)) }
        advanceUntilIdle()
        assertEquals(1, calls)
        assertContentEquals(byteArrayOf(1), store.bytes["job"])
    }

    @Test fun workerRedeliveryAndRestartNeverReplayPost() = runTest {
        val store = MemoryStore()
        var calls = 0
        val queue = PersistentUploadQueue(store, this, schedule = {}) { _, _ ->
            calls++; flowOf(Result.Error(uploadError(502, "uncertain")))
        }
        queue.enqueue(record(), byteArrayOf(1))
        queue.process("job")
        queue.process("job")
        val restored = PersistentUploadQueue(store, this, schedule = {}) { _, _ -> calls++; flowOf() }
        restored.process("job")
        advanceUntilIdle()
        assertEquals(1, calls)
        assertNotNull(store.bytes["job"])
    }

    @Test fun explicitRetryIsSingleFlightAndKeepsStableIdentity() = runTest {
        val store = MemoryStore()
        var calls = 0
        val queue = PersistentUploadQueue(store, this) { _, _ ->
            calls++; flowOf(Result.Error(uploadError(502, "uncertain")))
        }
        queue.enqueue(record(), byteArrayOf(1))
        advanceUntilIdle()
        queue.retry("job")
        queue.retry("job")
        advanceUntilIdle()
        assertEquals(2, calls)
        assertEquals("job", queue.snapshots.value.single().record.id)
        assertNotNull(store.bytes["job"])
    }

    @Test fun reconciliationPersistsConfirmationBeforeDeletionWithoutPost() = runTest {
        val store = MemoryStore()
        store.save(record().copy(state = UploadRecord.State.FAILED))
        store.saveBytes("job", byteArrayOf(1))
        var calls = 0
        val queue = PersistentUploadQueue(store, this) { _, _ -> calls++; flowOf() }
        queue.reconcile("job") { _, _ -> photo }
        queue.retry("job")
        advanceUntilIdle()
        assertEquals(0, calls)
        assertEquals(UploadRecord.State.COMPLETED, store.records["job"]?.state)
        assertNull(store.bytes["job"])
        assertEquals("save:COMPLETED", store.events[store.events.indexOf("delete:job") - 1])
    }

    @Test fun screenCleanupDoesNotCancelOrRemoveOtherEvidence() = runTest {
        val store = MemoryStore()
        val queue = PersistentUploadQueue(store, this, schedule = {}) { _, _ -> flowOf() }
        queue.enqueue(record(), byteArrayOf(1))
        queue.dismissCompleted()
        assertEquals(UploadRecord.State.QUEUED, store.records["job"]?.state)
        assertNotNull(store.bytes["job"])
    }

    @Test fun queuedRestartRequiresReviewAndCancellationBeforeWorkerPreventsHttp() = runTest {
        val store = MemoryStore()
        var calls = 0
        val queue = PersistentUploadQueue(store, this, schedule = {}) { _, _ -> calls++; flowOf() }
        queue.enqueue(record(), byteArrayOf(1))
        queue.cancel("job")
        queue.process("job")
        queue.enqueue(record("pending"), byteArrayOf(2))
        val restored = PersistentUploadQueue(store, this, schedule = {}) { _, _ -> calls++; flowOf() }
        restored.process("pending")
        assertEquals(0, calls)
        assertEquals(UploadRecord.State.FAILED, store.records["pending"]?.state)
        assertEquals(2, store.bytes.size)
    }

    @Test fun samePositionUsesDistinctEvidenceAndRestartKeepsBoth() = runTest {
        val store = MemoryStore()
        val queue = PersistentUploadQueue(store, this, schedule = {}) { _, _ -> flowOf() }
        queue.enqueue(record("first"), byteArrayOf(1))
        queue.enqueue(record("second"), byteArrayOf(2))
        val restored = PersistentUploadQueue(store, this, schedule = {}) { _, _ -> error("No replay") }
        assertEquals(2, restored.snapshots.value.size)
        assertContentEquals(byteArrayOf(1), store.bytes["first"])
        assertContentEquals(byteArrayOf(2), store.bytes["second"])
    }

    @Test fun inFlightCancelAndRestartDoNotRepeatUncertainTransfer() = runTest {
        val store = MemoryStore()
        var calls = 0
        val queue = PersistentUploadQueue(store, this) { _, _ -> flow {
            calls++
            emit(Result.Success(ProgressUpdate(1, 1)))
            CompletableDeferred<Unit>().await()
        } }
        queue.enqueue(record(), byteArrayOf(1))
        runCurrent()
        queue.cancel("job")
        queue.retry("job") // Cancellation has not finished; cannot create another job.
        advanceUntilIdle()
        val restored = PersistentUploadQueue(store, this) { _, _ -> calls++; flowOf() }
        advanceUntilIdle()
        assertEquals(1, calls)
        assertEquals(UploadRecord.State.CANCELED, restored.snapshots.value.single().record.state)
        assertNotNull(store.bytes["job"])
    }

    @Test fun recoveryExcludesRetryAndOtherRecoveryUntilGetCompletes() = runTest {
        val store = MemoryStore()
        store.save(record().copy(state = UploadRecord.State.FAILED))
        store.saveBytes("job", byteArrayOf(1))
        var posts = 0
        val queue = PersistentUploadQueue(store, this) { _, _ -> posts++; flowOf() }
        val gate = CompletableDeferred<Unit>()
        val recovery = launch {
            assertTrue(queue.reconcile("job") { _, _ -> gate.await(); photo })
        }
        runCurrent()
        queue.retry("job")
        assertFalse(queue.reconcile("job") { _, _ -> error("Concurrent GET") })
        gate.complete(Unit)
        recovery.join()
        queue.retry("job")
        advanceUntilIdle()
        assertEquals(0, posts)
        assertNull(store.bytes["job"])
    }

    @Test fun recoveryCannotInspectAnActivePostAndLateSuccessAfterCancelKeepsEvidence() = runTest {
        val store = MemoryStore()
        val gate = CompletableDeferred<Unit>()
        val queue = PersistentUploadQueue(store, this) { _, _ -> flow {
            kotlinx.coroutines.withContext(kotlinx.coroutines.NonCancellable) { gate.await() }
            emit(Result.Success(ProgressUpdate(1, 1, photo)))
        } }
        queue.enqueue(record(), byteArrayOf(1))
        runCurrent()
        assertFalse(queue.reconcile("job") { _, _ -> error("Active POST") })
        queue.cancel("job")
        assertFalse(queue.reconcile("job") { _, _ -> error("Cancellation still running") })
        gate.complete(Unit)
        advanceUntilIdle()
        assertEquals(UploadRecord.State.CANCELED, store.records["job"]?.state)
        assertNotNull(store.bytes["job"])
    }

    @Test fun prePostIdentitySnapshotIsDurableOnUncertainFailure() = runTest {
        val store = MemoryStore()
        val queue = PersistentUploadQueue(store, this, prepare = { setOf("old") }) { _, _ ->
            assertEquals(setOf("old"), store.records["job"]?.previousImageIds)
            flowOf(Result.Error(uploadError(502, "uncertain")))
        }
        queue.enqueue(record(), byteArrayOf(1))
        advanceUntilIdle()
        assertEquals(setOf("old"), store.records["job"]?.previousImageIds)
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
