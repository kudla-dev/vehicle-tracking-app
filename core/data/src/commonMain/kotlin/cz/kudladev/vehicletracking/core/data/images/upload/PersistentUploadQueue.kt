package cz.kudladev.vehicletracking.core.data.images.upload

import cz.kudladev.vehicletracking.core.domain.vehicles.ProgressUpdate
import cz.kudladev.vehicletracking.model.ErrorMessage
import cz.kudladev.vehicletracking.model.ImageWithUrl
import cz.kudladev.vehicletracking.model.Result
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

@Serializable
internal data class UploadRecord(
    val id: String,
    val tag: String,
    val vehicleId: Long? = null,
    val trackingId: String? = null,
    val trackingState: String? = null,
    val position: Int,
    val sourceUrl: String? = null,
    val state: State = State.QUEUED,
    val image: ImageWithUrl? = null,
    val error: ErrorMessage? = null,
) {
    @Serializable enum class State { QUEUED, UPLOADING, COMPLETED, FAILED, CANCELED }
}

internal data class UploadSnapshot(val record: UploadRecord, val progress: Float = 0f)

internal interface UploadStore {
    fun load(): List<UploadRecord>
    fun save(record: UploadRecord)
    fun saveBytes(id: String, bytes: ByteArray)
    fun readBytes(id: String): ByteArray
    fun deleteBytes(id: String)
    fun deleteRecord(id: String)
}

/** Durable evidence, application-lifetime execution. Not an iOS background URLSession. */
internal class PersistentUploadQueue(
    private val store: UploadStore,
    private val scope: CoroutineScope,
    private val transfer: (UploadRecord, ByteArray?) -> Flow<Result<ProgressUpdate, ErrorMessage>>,
) {
    private val mutex = Mutex()
    private val serialTransfer = Mutex()
    private val jobs = mutableMapOf<String, Job>()
    private val mutable = MutableStateFlow<List<UploadSnapshot>>(emptyList())
    val snapshots = mutable.asStateFlow()

    init {
        // Never replay non-idempotent POSTs after an uncertain process termination.
        mutable.value = store.load().map { record ->
            val restored = if (record.state in listOf(UploadRecord.State.QUEUED, UploadRecord.State.UPLOADING)) {
                record.copy(state = UploadRecord.State.FAILED,
                    error = uploadError(409, "Interrupted upload requires review before resubmission"))
            } else record
            store.save(restored)
            UploadSnapshot(restored, if (restored.state == UploadRecord.State.COMPLETED) 1f else 0f)
        }
    }

    suspend fun enqueue(record: UploadRecord, bytes: ByteArray?) = mutex.withLock {
        require(mutable.value.none { it.record.id == record.id }) { "Duplicate upload ID" }
        if (record.sourceUrl == null) {
            require(bytes != null && bytes.isNotEmpty()) { "Empty image" }
            store.saveBytes(record.id, requireNotNull(bytes))
        }
        // Persist bytes before publishing the job; a write failure must not start HTTP.
        store.save(record)
        publish(UploadSnapshot(record))
        jobs[record.id] = scope.launch {
            try {
                serialTransfer.withLock { run(record) }
            } finally {
                withContext(NonCancellable) { mutex.withLock { jobs.remove(record.id) } }
            }
        }
    }

    private suspend fun run(record: UploadRecord) {
        try {
            currentCoroutineContext().ensureActive()
            mutex.withLock { transition(record.copy(state = UploadRecord.State.UPLOADING)) }
            val result = transfer(record, if (record.sourceUrl == null) store.readBytes(record.id) else null)
                .awaitUploadedImage { update ->
                    mutex.withLock {
                        val current = mutable.value.single { it.record.id == record.id }
                        val progress = if (update.totalBytes > 0) {
                            (update.byteSent.toFloat() / update.totalBytes).coerceIn(0f, 1f)
                        } else current.progress
                        publish(current.copy(progress = progress))
                    }
                }
            mutex.withLock {
                when (result) {
                    is Result.Success -> {
                        transition(record.copy(state = UploadRecord.State.COMPLETED, image = result.data))
                        // Durable acknowledgement must precede deletion of evidence.
                        store.deleteBytes(record.id)
                    }
                    is Result.Error -> transition(record.copy(state = UploadRecord.State.FAILED, error = result.error))
                    Result.Loading -> error("Unexpected terminal upload state")
                }
            }
        } catch (e: CancellationException) {
            withContext(NonCancellable) {
                mutex.withLock {
                    val current = mutable.value.single { it.record.id == record.id }.record
                    if (current.state != UploadRecord.State.COMPLETED) {
                        cancelRecord(current)
                    }
                }
            }
            throw e
        } catch (e: Exception) {
            mutex.withLock {
                val current = mutable.value.single { it.record.id == record.id }.record
                if (current.state != UploadRecord.State.COMPLETED) {
                    val failed = current.copy(state = UploadRecord.State.FAILED,
                        error = uploadError(500, "Upload failed; local image retained"))
                    // A broken disk must not turn failure into apparent success.
                    try { store.save(failed) } catch (_: Exception) { }
                    publish(UploadSnapshot(failed))
                }
            }
        }
    }

    suspend fun cancel(id: String) = mutex.withLock {
        val current = mutable.value.find { it.record.id == id } ?: return@withLock
        if (current.record.state == UploadRecord.State.COMPLETED) return@withLock
        jobs[id]?.cancel()
        cancelRecord(current.record)
    }

    suspend fun clear() = mutex.withLock {
        jobs.values.forEach { it.cancel() }
        mutable.value.forEach { snapshot ->
            val record = snapshot.record
            if (record.state == UploadRecord.State.COMPLETED) {
                store.deleteBytes(record.id)
                store.deleteRecord(record.id)
            } else if (record.state in listOf(UploadRecord.State.QUEUED, UploadRecord.State.UPLOADING)) {
                cancelRecord(record)
            }
        }
        // Failed/canceled evidence stays on disk for recovery; hide only finished successes.
        mutable.value = mutable.value.filter { it.record.state != UploadRecord.State.COMPLETED }
    }

    private fun cancelRecord(record: UploadRecord) {
        try {
            transition(record.copy(state = UploadRecord.State.CANCELED))
        } catch (_: Exception) {
            publish(UploadSnapshot(record.copy(state = UploadRecord.State.FAILED,
                error = uploadError(500, "Cancellation could not be persisted; local image retained"))))
        }
    }

    private fun transition(record: UploadRecord) {
        store.save(record)
        publish(UploadSnapshot(record, if (record.state == UploadRecord.State.COMPLETED) 1f else 0f))
    }

    private fun publish(snapshot: UploadSnapshot) {
        mutable.value = mutable.value.filterNot { it.record.id == snapshot.record.id } + snapshot
    }
}
