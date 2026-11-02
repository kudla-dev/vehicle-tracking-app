package cz.kudladev.vehicletracking.core.datastore

import kotlinx.cinterop.*
import platform.CoreFoundation.*
import platform.Foundation.*
import platform.Security.*

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
class IosSecureTokenStorage(private val scope: StorageScope) : SecureTokenStorage {

    private val serviceName = "cz.kudladev.vehicletracking.${scope.key}"

    override suspend fun getAccessToken(): String? = get(KEY_ACCESS_TOKEN)
    override suspend fun getRefreshToken(): String? = get(KEY_REFRESH_TOKEN)
    override suspend fun getTokens(): Pair<String?, String?> = get(KEY_ACCESS_TOKEN) to get(KEY_REFRESH_TOKEN)

    override suspend fun saveAccessToken(token: String) {
        save(KEY_ACCESS_TOKEN, token)
    }

    override suspend fun saveRefreshToken(token: String) {
        save(KEY_REFRESH_TOKEN, token)
    }

    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        save(KEY_ACCESS_TOKEN, accessToken)
        save(KEY_REFRESH_TOKEN, refreshToken)
    }

    override suspend fun clearTokens() {
        delete(KEY_ACCESS_TOKEN)
        delete(KEY_REFRESH_TOKEN)
    }

    private fun save(account: String, value: String) {
        val data = (value as NSString).dataUsingEncoding(NSUTF8StringEncoding) ?: return
        delete(account)

        val query = CFDictionaryCreateMutable(null, 0, null, null)
        val serviceCf = CFBridgingRetain(serviceName as NSString)
        val accountCf = CFBridgingRetain(account as NSString)
        val dataCf = CFBridgingRetain(data)
        try {
            CFDictionarySetValue(query, kSecClass, kSecClassGenericPassword)
            CFDictionarySetValue(query, kSecAttrService, serviceCf)
            CFDictionarySetValue(query, kSecAttrAccount, accountCf)
            CFDictionarySetValue(query, kSecValueData, dataCf)
            CFDictionarySetValue(query, kSecAttrAccessible, kSecAttrAccessibleAfterFirstUnlock)
            SecItemAdd(query, null)
        } finally {
            CFRelease(query)
            CFRelease(serviceCf)
            CFRelease(accountCf)
            CFRelease(dataCf)
        }
    }

    private fun get(account: String): String? {
        val query = CFDictionaryCreateMutable(null, 0, null, null)
        val serviceCf = CFBridgingRetain(serviceName as NSString)
        val accountCf = CFBridgingRetain(account as NSString)
        try {
            CFDictionarySetValue(query, kSecClass, kSecClassGenericPassword)
            CFDictionarySetValue(query, kSecAttrService, serviceCf)
            CFDictionarySetValue(query, kSecAttrAccount, accountCf)
            CFDictionarySetValue(query, kSecReturnData, kCFBooleanTrue)
            CFDictionarySetValue(query, kSecMatchLimit, kSecMatchLimitOne)

            memScoped {
                val result = alloc<CFTypeRefVar>()
                val status = SecItemCopyMatching(query, result.ptr)
                if (status == errSecSuccess) {
                    val cfData = result.value
                    if (cfData != null) {
                        val nsData = CFBridgingRelease(cfData) as? NSData
                        if (nsData != null) {
                            return NSString.create(nsData, NSUTF8StringEncoding)?.toString()
                        }
                    }
                }
            }
            return null
        } finally {
            CFRelease(query)
            CFRelease(serviceCf)
            CFRelease(accountCf)
        }
    }

    private fun delete(account: String) {
        val query = CFDictionaryCreateMutable(null, 0, null, null)
        val serviceCf = CFBridgingRetain(serviceName as NSString)
        val accountCf = CFBridgingRetain(account as NSString)
        try {
            CFDictionarySetValue(query, kSecClass, kSecClassGenericPassword)
            CFDictionarySetValue(query, kSecAttrService, serviceCf)
            CFDictionarySetValue(query, kSecAttrAccount, accountCf)
            SecItemDelete(query)
        } finally {
            CFRelease(query)
            CFRelease(serviceCf)
            CFRelease(accountCf)
        }
    }

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
    }
}

actual fun createSecureTokenStorage(context: Any?, scope: StorageScope): SecureTokenStorage {
    return IosSecureTokenStorage(scope)
}
