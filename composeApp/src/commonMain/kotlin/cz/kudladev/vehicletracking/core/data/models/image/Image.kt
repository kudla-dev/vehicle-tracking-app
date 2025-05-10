package cz.kudladev.vehicletracking.core.data.models.image

interface Image {

}

data class ImageWithUrl(
    val id: String? = null,
    val url: String? = null,
): Image

data class ImageWithBytes(
    val id: String? = null,
    val bytes: ByteArray? = null,
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