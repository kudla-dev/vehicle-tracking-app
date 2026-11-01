package cz.kudladev.vehicletracking.di
import cz.kudladev.vehicletracking.network.AppConfig
import cz.kudladev.vehicletracking.mock.mockNetworkModule
import org.koin.core.module.Module
actual fun hostTransport(config: AppConfig): Module = mockNetworkModule(config, cz.kudladev.vehicletracking.mock.MockServer.Scenario.valueOf(platform.Foundation.NSProcessInfo.processInfo.environment["MOTOPARK_MOCK_SCENARIO"] as? String ?: "NORMAL"))
