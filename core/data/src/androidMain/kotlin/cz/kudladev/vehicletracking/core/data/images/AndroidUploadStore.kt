package cz.kudladev.vehicletracking.core.data.images

import cz.kudladev.vehicletracking.core.datastore.StorageScope
import android.content.Context
import android.util.AtomicFile
import cz.kudladev.vehicletracking.core.data.images.upload.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.util.UUID

internal class AndroidUploadStore(context: Context, scope: StorageScope) : UploadStore {

    private val directory = File(context.filesDir, "${scope.directory}/image-uploads").apply { check(isDirectory || mkdirs()) }
    private val json = Json { ignoreUnknownKeys = true }
    private fun file(id: String, suffix: String): File {
        require(id.isNotEmpty() && id.all { it.isLetterOrDigit() || it == '-' })
        return File(directory, "$id.$suffix")
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
    }
    override fun deleteRecord(id: String) { AtomicFile(file(id, "json")).delete() }
    private fun write(file: File, bytes: ByteArray) {
        val atomic = AtomicFile(file)
        val stream = atomic.startWrite()
        try { stream.write(bytes); atomic.finishWrite(stream) }
        catch (e: Exception) { atomic.failWrite(stream); throw e }
    }
}
