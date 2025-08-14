package cz.kudladev.vehicletracking.feature.menu.main.di

import cz.kudladev.vehicletracking.feature.menu.main.MenuScreenViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val menuModule = module {
    viewModelOf(::MenuScreenViewModel)
}