package cz.kudladev.vehicletracking

import android.app.Application
import cz.kudladev.vehicletracking.di.appModules
import cz.kudladev.vehicletracking.network.AppConfig
import cz.kudladev.vehicletracking.network.AppEnvironment
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class VehicleTrackingApp: Application() {

    init {
        startKoin {
            modules(
                appModules(AppConfig.create(AppEnvironment.DEV, BuildConfig.API_BASE_URL))
            )
            androidContext(this@VehicleTrackingApp)
        }
    }

    override fun onCreate() {
        super.onCreate()
    }
}
