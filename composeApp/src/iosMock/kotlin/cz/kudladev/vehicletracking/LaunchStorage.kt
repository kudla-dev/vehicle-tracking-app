package cz.kudladev.vehicletracking
import cz.kudladev.vehicletracking.network.AppConfig
import cz.kudladev.vehicletracking.network.AppEnvironment
import platform.Foundation.*
import kotlinx.cinterop.ExperimentalForeignApi
@OptIn(ExperimentalForeignApi::class)
fun prepareLaunchStorage(config: AppConfig) {
    if (config.environment != AppEnvironment.MOCK) return
    val files = NSFileManager.defaultManager
    val root = requireNotNull(files.URLForDirectory(NSApplicationSupportDirectory, NSUserDomainMask, null, true, null)?.path)
    val directory = "$root/${config.storageScope.directory}"
    if (files.fileExistsAtPath(directory)) check(files.moveItemAtPath(directory, "$root/mock-archive-${NSUUID().UUIDString}", null))
}
