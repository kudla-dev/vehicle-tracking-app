package cz.kudladev.vehicletracking.mock

import cz.kudladev.vehicletracking.network.*
import io.ktor.client.HttpClient
import org.koin.dsl.module

fun mockNetworkModule(config: AppConfig, scenario: MockServer.Scenario = MockServer.Scenario.NORMAL) = module {
    require(config.environment == AppEnvironment.MOCK && config.api == null)
    single { config }
    single { config.storageScope }
    single { MockServer.api }
    single(createdAtStart = true) {
        MockServer(scenario).also { server ->
            cz.kudladev.vehicletracking.model.AppClock.clock = object : kotlinx.datetime.Clock {
                override fun now() = server.now
            }
        }
    }
    single<HttpClient> { HttpClientFactory(get(), MockServer.api).create(get<MockServer>().engine()) }
}
