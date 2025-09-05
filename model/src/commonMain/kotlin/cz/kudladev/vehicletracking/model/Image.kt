package cz.kudladev.vehicletracking.model

import kotlinx.serialization.Serializable

interface Image

@Serializable
data class ImageWithUrl(
    val id: String? = null,
    val url: String? = null,
    val position: Int? = null
): Image

data class ImageWithBytes(
    val id: String? = null,
    val bytes: ByteArray? = null,
    val position: Int? = null
): Image {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ImageWithBytes

        if (id != other.id) return false
        if (!bytes.contentEquals(other.bytes)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (bytes?.contentHashCode() ?: 0)
        return result
    }
}

// core/domain/images/ImageUploadStatus.kt
sealed class ImageUploadState {
    data object Queued : ImageUploadState()
    data class Uploading(val imageUri: ByteArray?, val progress: Float) : ImageUploadState()
    data class Completed(val imageURL: Image) : ImageUploadState()
    data class Failed(val errorMessage: ErrorMessage) : ImageUploadState()
    data class Canceled(val imageUri: String) : ImageUploadState()
}