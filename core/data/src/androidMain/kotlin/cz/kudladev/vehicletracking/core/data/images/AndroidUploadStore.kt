package cz.kudladev.vehicletracking.core.data.images

import android.content.Context
import android.util.AtomicFile
import cz.kudladev.vehicletracking.core.data.images.upload.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.util.UUID

internal class AndroidUploadStore(context: Context) : UploadStore {
    private val legacyDirectory = context.filesDir
    private val directory = File(context.filesDir, "image-uploads").apply { check(isDirectory || mkdirs()) }
    private val json = Json { ignoreUnknownKeys = true }
    private fun file(id: String, suffix: String): File {
        require(id.isNotEmpty() && id.all { it.isLetterOrDigit() || it == '-' })
        return File(directory, "$id.$suffix")
    }
    init {
        // Older WorkManager jobs did not keep a durable target journal. Import their evidence
        // conservatively; never infer a vehicle target from a timestamp-only filename.
        context.filesDir.listFiles().orEmpty().filter { it.name.startsWith("temp_image_") && it.extension == "jpg" }
            .forEach { legacy ->
                val id = UUID.nameUUIDFromBytes(legacy.name.encodeToByteArray()).toString()
                if (!file(id, "json").exists() && !file(id, "imported").exists()) {
                    val match = Regex("temp_image_(.+)_(active|returned)_(\\d+)\\.jpg").matchEntire(legacy.name)
                    saveBytes(id, legacy.readBytes())
                    save(UploadRecord(id,
                        match?.let { "tracking_image_upload_${it.groupValues[1]}_${it.groupValues[2]}" } ?: "image_upload",
                        trackingId = match?.groupValues?.get(1), trackingState = match?.groupValues?.get(2),
                        position = match?.groupValues?.get(3)?.toIntOrNull() ?: -1,
                        state = UploadRecord.State.FAILED,
                        error = uploadError(409, "Původní přenos vyžaduje prověření; fotografie zachována. U neznámého cíle je nutná ruční obnova.")))
                }
                file(id, "imported").writeText("imported")
            }
    }
    override fun load(): List<UploadRecord> {
        directory.listFiles().orEmpty().filter { it.extension == "jpg" }.forEach { photo ->
            if (!file(photo.nameWithoutExtension, "json").exists()) {
                save(UploadRecord(photo.nameWithoutExtension, "image_upload", position = -1,
                    state = UploadRecord.State.FAILED,
                    error = uploadError(409, "Fotografie bez metadat cíle; nutná ruční obnova.")))
            }
        }
        return directory.listFiles().orEmpty()
        .filter { it.extension == "json" }.sortedBy { it.name }.map { file ->
            try {
                json.decodeFromString<UploadRecord>(AtomicFile(file).readFully().decodeToString()).also {
                    require(it.id == file.nameWithoutExtension)
                    if (it.state == UploadRecord.State.COMPLETED) require(!it.image?.id.isNullOrBlank() && !it.image?.url.isNullOrBlank())
                }
            } catch (_: Exception) {
                check(file.renameTo(File(directory, "${file.name}.${UUID.randomUUID()}.corrupt")))
                UploadRecord(file.nameWithoutExtension, "image_upload", position = -1,
                    state = UploadRecord.State.FAILED, error = uploadError(500, "Upload metadata unreadable; photo retained"))
            }
        }
    }
    override fun save(record: UploadRecord) = write(file(record.id, "json"), json.encodeToString(record).encodeToByteArray())
    override fun saveBytes(id: String, bytes: ByteArray) = write(file(id, "jpg"), bytes)
    override fun readBytes(id: String) = AtomicFile(file(id, "jpg")).readFully()
    override fun deleteBytes(id: String) {
        AtomicFile(file(id, "jpg")).delete()
        legacyDirectory.listFiles().orEmpty().filter {
            it.name.startsWith("temp_image_") && it.extension == "jpg" &&
                UUID.nameUUIDFromBytes(it.name.encodeToByteArray()).toString() == id
        }.forEach { check(it.delete()) { "Cannot delete confirmed legacy photo" } }
    }
    override fun deleteRecord(id: String) { AtomicFile(file(id, "json")).delete() }
    private fun write(file: File, bytes: ByteArray) {
        val atomic = AtomicFile(file)
        val stream = atomic.startWrite()
        try { stream.write(bytes); atomic.finishWrite(stream) }
        catch (e: Exception) { atomic.failWrite(stream); throw e }
    }
}
