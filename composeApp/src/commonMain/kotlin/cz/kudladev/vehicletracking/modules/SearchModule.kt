package cz.kudladev.vehicletracking.modules

import cz.kudladev.vehicletracking.search.SearchScreenViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val searchModule = module {

    viewModelOf(::SearchScreenViewModel)

}