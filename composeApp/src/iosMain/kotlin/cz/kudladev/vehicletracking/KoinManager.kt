package cz.kudladev.vehicletracking

import cz.kudladev.vehicletracking.modules.authModule
import cz.kudladev.vehicletracking.modules.menuModule
import cz.kudladev.vehicletracking.modules.networkModule
import cz.kudladev.vehicletracking.modules.platformModule
import cz.kudladev.vehicletracking.modules.searchModule
import cz.kudladev.vehicletracking.modules.validationModule
import cz.kudladev.vehicletracking.modules.vehicleModule
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

fun initKoin(){
    stopKoin()
    startKoin {
        modules(
            platformModule,
            networkModule,
            authModule,
            validationModule,
            menuModule,
            vehicleModule,
            searchModule
        )
    }
}