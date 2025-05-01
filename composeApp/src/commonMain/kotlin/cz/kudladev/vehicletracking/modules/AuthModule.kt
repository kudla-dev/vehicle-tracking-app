package cz.kudladev.vehicletracking.modules

import cz.kudladev.vehicletracking.auth.data.AuthApi
import cz.kudladev.vehicletracking.auth.data.AuthRepositoryImpl
import cz.kudladev.vehicletracking.auth.data.UserStateHolderImpl
import cz.kudladev.vehicletracking.auth.domain.AuthRepository
import cz.kudladev.vehicletracking.auth.domain.UserStateHolder
import cz.kudladev.vehicletracking.auth.presentation.login.LoginScreenViewModel
import cz.kudladev.vehicletracking.auth.presentation.register.RegisterScreenViewModel
import de.jensklingenberg.ktorfit.Ktorfit
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val authModule = module {

    singleOf(::UserStateHolderImpl).bind(UserStateHolder::class)

    single<AuthApi> {
        get<Ktorfit>()
            .create<AuthApi>()
    }

    singleOf(::AuthRepositoryImpl).bind(AuthRepository::class)

    viewModelOf(::RegisterScreenViewModel)
    viewModelOf(::LoginScreenViewModel)
}