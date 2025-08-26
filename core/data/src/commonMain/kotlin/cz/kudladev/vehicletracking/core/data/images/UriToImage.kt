package cz.kudladev.vehicletracking.core.data.images

expect suspend fun uriToImage(
    uri: String
): ByteArray?