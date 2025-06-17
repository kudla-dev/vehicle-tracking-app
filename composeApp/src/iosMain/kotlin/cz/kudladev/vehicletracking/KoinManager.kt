package cz.kudladev.vehicletracking

import cz.kudladev.vehicletracking.di.appModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

fun initKoin(){
    stopKoin()
    startKoin {
        modules(
            appModules
        )
    }
}