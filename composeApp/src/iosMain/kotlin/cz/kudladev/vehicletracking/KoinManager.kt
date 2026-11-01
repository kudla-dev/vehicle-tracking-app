package cz.kudladev.vehicletracking

import cz.kudladev.vehicletracking.di.appModules
import cz.kudladev.vehicletracking.network.AppConfig
import cz.kudladev.vehicletracking.network.AppEnvironment
import platform.Foundation.NSBundle
import platform.Foundation.NSProcessInfo
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

private fun launchConfig(): AppConfig {
    val environment = AppEnvironment.valueOf(requireNotNull(
        NSBundle.mainBundle.objectForInfoDictionaryKey("MotoparkEnvironment") as? String
    ) { "Missing build environment" })
    if (environment == AppEnvironment.MOCK) return AppConfig.create(environment)
    val override = NSProcessInfo.processInfo.environment["MOTOPARK_API_BASE_URL"] as? String
    val bundled = NSBundle.mainBundle.objectForInfoDictionaryKey("MotoparkAPIBaseURL") as? String
    return AppConfig.create(environment, override ?: bundled)
}

internal val iosAppConfig by lazy { launchConfig() }

fun initKoin() {
    val config = iosAppConfig
    prepareLaunchStorage(config)
    stopKoin()
    startKoin { modules(appModules(config)) }
}
