package cz.kudladev.vehicletracking.di

import cz.kudladev.vehicletracking.core.data.di.dataModule
import cz.kudladev.vehicletracking.feature.history.di.historyModule
import cz.kudladev.vehicletracking.feature.menu.admin_settings.di.adminSettingsModule
import cz.kudladev.vehicletracking.feature.menu.manage_trackings.di.manageTrackingsModule
import cz.kudladev.vehicletracking.feature.menu.manage_vehicles.di.addEditVehicleModule
import cz.kudladev.vehicletracking.feature.menu.protocols.di.protocolsModule
import cz.kudladev.vehicletracking.feature.menu.tracking_detail.di.trackingDetailModule
import cz.kudladev.vehicletracking.feature.onboarding.di.onBoardingModule
import cz.kudladev.vehicletracking.feature.search.di.searchModule
import cz.kudladev.vehicletracking.feature.tracking.di.trackingModule
import cz.kudladev.vehicletracking.feature.vehicledetail.di.vehicleDetailModule
import cz.kudladev.vehicletracking.feature.vehicles.di.vehiclesModule
import cz.kudladev.vehicletracking.network.di.networkModule
import org.koin.dsl.module


val featureModule = module {
    includes(onBoardingModule)
    includes(vehiclesModule)
    includes(historyModule)
    includes(vehicleDetailModule)
    includes(searchModule)
    includes(adminSettingsModule)
    includes(addEditVehicleModule)
    includes(trackingModule)
    includes(manageTrackingsModule)
    includes(trackingDetailModule)
    includes(protocolsModule)
}
val appModules = listOf(
    dataModule,
    networkModule,
    featureModule
)


