package cz.kudladev.vehicletracking.security

actual object Crypto {
    actual fun decrypt(bytes: ByteArray): ByteArray {
        return ByteArray(0)
    }

    actual fun encrypt(bytes: ByteArray): ByteArray {
        return ByteArray(0)
    }

}