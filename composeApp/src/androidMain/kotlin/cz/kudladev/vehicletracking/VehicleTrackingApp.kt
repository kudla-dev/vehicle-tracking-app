package cz.kudladev.vehicletracking

import android.app.Application
import cz.kudladev.vehicletracking.core.datastore.AppContext
import cz.kudladev.vehicletracking.di.appModules
import cz.kudladev.vehicletracking.network.AppConfig
import cz.kudladev.vehicletracking.network.AppEnvironment
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class VehicleTrackingApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppContext.set(this)
        val environment = AppEnvironment.valueOf(BuildConfig.APP_ENVIRONMENT)
        val config = AppConfig.create(environment, BuildConfig.API_BASE_URL.takeIf { it.isNotEmpty() })
        prepareLaunchStorage(config, filesDir)
        startKoin {
            androidContext(this@VehicleTrackingApp)
            modules(appModules(config))
        }
    }
}
