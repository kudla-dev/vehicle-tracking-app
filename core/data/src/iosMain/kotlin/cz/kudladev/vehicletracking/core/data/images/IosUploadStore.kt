package cz.kudladev.vehicletracking.core.data.images

import cz.kudladev.vehicletracking.core.datastore.StorageScope
import cz.kudladev.vehicletracking.core.data.images.upload.UploadRecord
import cz.kudladev.vehicletracking.core.data.images.upload.UploadStore
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import platform.Foundation.NSUUID
import cz.kudladev.vehicletracking.core.data.images.upload.uploadError
import platform.Foundation.create
import platform.Foundation.dataWithContentsOfFile
import platform.Foundation.writeToFile
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
internal class IosUploadStore(scope: StorageScope) : UploadStore {
    private val files = NSFileManager.defaultManager
    private val directory = requireNotNull(files.URLForDirectory(
        NSApplicationSupportDirectory, NSUserDomainMask, null, true, null
    )?.path) + "/${scope.directory}/image-uploads"
    private val json = Json { ignoreUnknownKeys = true }

    init {
        check(files.createDirectoryAtPath(directory, true, null, null)) { "Cannot create upload directory" }
    }

    override fun load(): List<UploadRecord> {
        files.contentsOfDirectoryAtPath(directory, null).orEmpty().filterIsInstance<String>()
            .filter { it.endsWith(".jpg") }.forEach { name ->
                val id = name.removeSuffix(".jpg")
                if (!files.fileExistsAtPath(path(id, "json"))) {
                    save(UploadRecord(id, "image_upload", position = -1, state = UploadRecord.State.FAILED,
                        error = uploadError(409, "Fotografie bez metadat cíle; nutná ruční obnova.")))
                }
            }
        return files.contentsOfDirectoryAtPath(directory, null).orEmpty()
        .filterIsInstance<String>().filter { it.endsWith(".json") }.sorted().map { name ->
            val id = name.removeSuffix(".json")
            try {
                json.decodeFromString<UploadRecord>(read("$directory/$name").decodeToString()).also {
                    require(it.id == id)
                    if (it.state == UploadRecord.State.COMPLETED) {
                        require(!it.image?.id.isNullOrBlank() && !it.image?.url.isNullOrBlank())
                    }
                }
            } catch (_: Exception) {
                // Quarantine metadata for manual recovery; never discard its associated photo.
                check(files.moveItemAtPath("$directory/$name", "$directory/$name.${NSUUID().UUIDString}.corrupt", null))
                UploadRecord(id, "image_upload", position = -1, state = UploadRecord.State.FAILED,
                    error = uploadError(500, "Upload metadata is unreadable; local evidence retained"))
            }
        }

    }

    override fun save(record: UploadRecord) = write(path(record.id, "json"), json.encodeToString(record).encodeToByteArray())
    override fun saveBytes(id: String, bytes: ByteArray) = write(path(id, "jpg"), bytes)
    override fun readBytes(id: String): ByteArray = read(path(id, "jpg"))
    override fun deleteBytes(id: String) { remove(path(id, "jpg")) }
    override fun deleteRecord(id: String) { remove(path(id, "json")) }

    private fun path(id: String, suffix: String): String {
        require(id.isNotEmpty() && id.all { it.isLetterOrDigit() || it == '-' }) { "Invalid upload ID" }
        return "$directory/$id.$suffix"
    }

    private fun read(path: String): ByteArray =
        requireNotNull(NSData.dataWithContentsOfFile(path)) { "Cannot read upload file" }.toByteArray()

    private fun write(path: String, bytes: ByteArray) {
        require(bytes.isNotEmpty())
        val data = bytes.usePinned { NSData.create(bytes = it.addressOf(0), length = bytes.size.toULong()) }
        check(data.writeToFile(path, atomically = true)) { "Cannot persist upload file" }
    }

    private fun remove(path: String) {
        if (files.fileExistsAtPath(path)) check(files.removeItemAtPath(path, null)) { "Cannot remove upload file" }
    }
}

@OptIn(ExperimentalForeignApi::class)
internal fun NSData.toByteArray(): ByteArray {
    require(length <= Int.MAX_VALUE.toULong())
    return ByteArray(length.toInt()).also { buffer ->
        if (buffer.isNotEmpty()) buffer.usePinned { memcpy(it.addressOf(0), bytes, length) }
    }
}
