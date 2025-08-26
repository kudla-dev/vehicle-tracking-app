package cz.kudladev.vehicletracking.core.data.images

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

actual suspend fun uriToImage(uri: String): ByteArray? = withContext(Dispatchers.IO){
    try {
        File(uri).readBytes()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}