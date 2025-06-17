package cz.kudladev.vehicletracking.feature.onboarding.di

import cz.kudladev.vehicletracking.core.domain.di.domainModule
import cz.kudladev.vehicletracking.feature.onboarding.loading.LoadingScreenViewModel
import cz.kudladev.vehicletracking.feature.onboarding.login.LoginScreenViewModel
import cz.kudladev.vehicletracking.feature.onboarding.register.RegisterScreenViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val onBoardingModule = module {
    includes(domainModule)

    viewModelOf(::LoadingScreenViewModel)
    viewModelOf(::LoginScreenViewModel)
    viewModelOf(::RegisterScreenViewModel)

}