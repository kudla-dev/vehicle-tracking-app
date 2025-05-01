package cz.kudladev.vehicletracking.modules

import cz.kudladev.vehicletracking.auth.domain.use_cases.EmailValidation
import cz.kudladev.vehicletracking.auth.domain.use_cases.FirstNameValidation
import cz.kudladev.vehicletracking.auth.domain.use_cases.LastNameValidation
import cz.kudladev.vehicletracking.auth.domain.use_cases.PasswordConfirmValidation
import cz.kudladev.vehicletracking.auth.domain.use_cases.PasswordValidation
import cz.kudladev.vehicletracking.auth.domain.use_cases.PhoneNumberValidation
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val validationModule = module {

    singleOf(::EmailValidation)
    singleOf(::FirstNameValidation)
    singleOf(::LastNameValidation)
    singleOf(::PasswordValidation)
    singleOf(::PhoneNumberValidation)
    singleOf(::PasswordConfirmValidation)

}