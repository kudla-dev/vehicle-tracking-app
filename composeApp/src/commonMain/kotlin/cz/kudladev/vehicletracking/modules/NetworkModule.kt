package cz.kudladev.vehicletracking.modules

import cz.kudladev.vehicletracking.network.HttpClientFactory
import cz.kudladev.vehicletracking.network.NetworkConstants
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import org.koin.dsl.module

val networkModule = module {
    single<HttpClient>{
        HttpClientFactory.create(get())
    }

    single<Ktorfit>{
        Ktorfit.Builder()
            .baseUrl(NetworkConstants.BASE_URL)
            .httpClient(client = get<HttpClient>())
            .build()
    }
}