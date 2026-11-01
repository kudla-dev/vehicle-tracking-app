package cz.kudladev.vehicletracking.di
import cz.kudladev.vehicletracking.network.AppConfig
import cz.kudladev.vehicletracking.network.di.networkModule
import org.koin.core.module.Module
actual fun hostTransport(config: AppConfig): Module = networkModule(config)
