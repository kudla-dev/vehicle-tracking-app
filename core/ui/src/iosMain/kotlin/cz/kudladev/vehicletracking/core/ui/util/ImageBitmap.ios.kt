package cz.kudladev.vehicletracking.core.ui.util

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image

actual fun ByteArray.toImageBitmap(): ImageBitmap? = try {
    Image.makeFromEncoded(this).toComposeImageBitmap()
} catch (_: Exception) {
    // An unreadable local preview must not crash the screen or discard the file.
    null
}
