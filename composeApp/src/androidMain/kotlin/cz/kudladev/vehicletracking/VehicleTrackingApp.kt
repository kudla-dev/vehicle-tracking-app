package cz.kudladev.vehicletracking

import android.app.Application
import cz.kudladev.vehicletracking.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class VehicleTrackingApp: Application() {

    init {
        startKoin {
            modules(
                appModules
            )
            androidContext(this@VehicleTrackingApp)
        }
    }

    override fun onCreate() {
        super.onCreate()
    }
}