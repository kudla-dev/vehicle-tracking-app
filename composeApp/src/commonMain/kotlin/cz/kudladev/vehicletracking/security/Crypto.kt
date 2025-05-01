package cz.kudladev.vehicletracking.security

expect object Crypto {
    fun encrypt(bytes: ByteArray): ByteArray
    fun decrypt(bytes: ByteArray): ByteArray
}
