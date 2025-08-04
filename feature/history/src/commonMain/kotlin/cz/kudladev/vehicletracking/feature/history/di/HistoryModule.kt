package cz.kudladev.vehicletracking.feature.history.di

import cz.kudladev.vehicletracking.feature.history.HistoryScreenViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val historyModule = module {
    viewModelOf(::HistoryScreenViewModel)
}