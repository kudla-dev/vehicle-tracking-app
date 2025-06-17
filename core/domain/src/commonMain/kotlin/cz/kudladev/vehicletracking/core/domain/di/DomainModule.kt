package cz.kudladev.vehicletracking.core.domain.di

import org.koin.dsl.module


val domainModule = module {

    includes(validationModule)

}