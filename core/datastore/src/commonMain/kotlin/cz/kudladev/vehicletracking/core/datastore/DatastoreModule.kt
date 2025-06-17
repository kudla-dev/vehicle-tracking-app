package cz.kudladev.vehicletracking.core.datastore

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val datastoreModule = module {
    // Add a definition for AppContext based on what it is:

    // If AppContext is a Kotlin object (singleton):
    single { AppContext }

    // OR if it's a class that needs instantiation:
    // singleOf(::AppContext)

    // OR if it needs context parameters:
    // single { AppContext(androidContext) }

    singleOf(::DataStoreRepository)
}