package cz.kudladev.vehicletracking.feature.search.di

import cz.kudladev.vehicletracking.core.domain.di.domainModule
import cz.kudladev.vehicletracking.feature.search.SearchScreenViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val searchModule = module {

    viewModelOf(::SearchScreenViewModel)

}