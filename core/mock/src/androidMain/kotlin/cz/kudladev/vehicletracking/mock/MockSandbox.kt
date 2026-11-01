package cz.kudladev.vehicletracking.mock
import cz.kudladev.vehicletracking.network.AppConfig
import cz.kudladev.vehicletracking.network.AppEnvironment
import java.io.File
import java.util.UUID
fun archiveMockSandbox(config: AppConfig, root: File) {
    if (config.environment != AppEnvironment.MOCK) return
    val directory = File(root, config.storageScope.directory)
    if (directory.exists()) check(directory.renameTo(File(root, "mock-archive-${UUID.randomUUID()}")))
}
