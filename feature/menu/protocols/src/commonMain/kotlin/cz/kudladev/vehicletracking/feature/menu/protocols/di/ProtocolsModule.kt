package cz.kudladev.vehicletracking.feature.menu.protocols.di

import cz.kudladev.vehicletracking.feature.menu.protocols.ProtocolsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val protocolsModule = module {

    viewModelOf(::ProtocolsViewModel)

}