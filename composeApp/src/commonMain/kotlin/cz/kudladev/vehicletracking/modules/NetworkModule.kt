package cz.kudladev.vehicletracking.modules

import cz.kudladev.vehicletracking.app.AppContext
import cz.kudladev.vehicletracking.datastore.DataStoreRepository
import cz.kudladev.vehicletracking.network.HttpClientFactory
import cz.kudladev.vehicletracking.network.NetworkConstants
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val networkModule = module {

    single<AppContext> {
        AppContext
    }

    singleOf(::DataStoreRepository)

    singleOf(::HttpClientFactory)

    single<HttpClient>{
        get<HttpClientFactory>()
        .create(get<HttpClientEngine>())
    }

}