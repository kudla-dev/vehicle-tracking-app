package cz.kudladev.vehicletracking
import cz.kudladev.vehicletracking.network.AppConfig
import java.io.File
fun prepareLaunchStorage(config: AppConfig, root: File) { cz.kudladev.vehicletracking.mock.archiveMockSandbox(config, root) }
