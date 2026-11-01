package cz.kudladev.vehicletracking

import cz.kudladev.vehicletracking.di.appModules
import cz.kudladev.vehicletracking.network.AppConfig
import cz.kudladev.vehicletracking.network.AppEnvironment
import platform.Foundation.NSBundle
import platform.Foundation.NSProcessInfo
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

fun initKoin(){
    val override = NSProcessInfo.processInfo.environment["MOTOPARK_API_BASE_URL"] as? String
    val bundled = NSBundle.mainBundle.objectForInfoDictionaryKey("MotoparkAPIBaseURL") as? String
    val config = AppConfig.create(AppEnvironment.DEV, override ?: bundled ?: "http://127.0.0.1:8080")
    stopKoin()
    startKoin {
        modules(
            appModules(config)
        )
    }
}
