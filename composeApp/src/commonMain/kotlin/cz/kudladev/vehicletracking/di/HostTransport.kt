package cz.kudladev.vehicletracking.di
import cz.kudladev.vehicletracking.network.AppConfig
import org.koin.core.module.Module
expect fun hostTransport(config: AppConfig): Module
