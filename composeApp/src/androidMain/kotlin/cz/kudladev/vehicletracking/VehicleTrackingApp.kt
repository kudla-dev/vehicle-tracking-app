package cz.kudladev.vehicletracking

import android.app.Application
import cz.kudladev.vehicletracking.app.AppContext
import cz.kudladev.vehicletracking.modules.authModule
import cz.kudladev.vehicletracking.modules.networkModule
import cz.kudladev.vehicletracking.modules.platformModule
import cz.kudladev.vehicletracking.modules.validationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class VehicleTrackingApp: Application() {
    override fun onCreate() {
        super.onCreate()


        startKoin {
            modules(
                platformModule,
                networkModule,
                authModule,
                validationModule
            )
            androidContext(this@VehicleTrackingApp)
        }
    }
}