package cz.kudladev.vehicletracking.core.datastore

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val datastoreModule = module {
    single { AppContext }
    single<SecureTokenStorage> { createSecureTokenStorage(getOrNull<AppContext>(), get()) }
    single { DataStoreRepository(get(), get(), get()) }
}
