package cz.kudladev.vehicletracking.core.data.images

import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL

actual suspend fun uriToImage(uri: String): ByteArray? {
    val url = if (uri.startsWith("file:")) NSURL.URLWithString(uri) else NSURL.fileURLWithPath(uri)
    if (url == null || !url.isFileURL()) return null
    return NSData.dataWithContentsOfURL(url)?.toByteArray()
}
