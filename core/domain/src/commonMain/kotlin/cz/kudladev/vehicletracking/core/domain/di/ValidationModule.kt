package cz.kudladev.vehicletracking.core.domain.di

import cz.kudladev.vehicletracking.core.domain.auth.validations.EmailValidation
import cz.kudladev.vehicletracking.core.domain.auth.validations.FirstNameValidation
import cz.kudladev.vehicletracking.core.domain.auth.validations.LastNameValidation
import cz.kudladev.vehicletracking.core.domain.auth.validations.PasswordConfirmValidation
import cz.kudladev.vehicletracking.core.domain.auth.validations.PasswordValidation
import cz.kudladev.vehicletracking.core.domain.auth.validations.PhoneNumberValidation
import cz.kudladev.vehicletracking.core.domain.vehicles.validations.BrandValidator
import cz.kudladev.vehicletracking.core.domain.vehicles.validations.ColorValidator
import cz.kudladev.vehicletracking.core.domain.vehicles.validations.FullNameValidator
import cz.kudladev.vehicletracking.core.domain.vehicles.validations.MaximumDistanceValidator
import cz.kudladev.vehicletracking.core.domain.vehicles.validations.ModelValidator
import cz.kudladev.vehicletracking.core.domain.vehicles.validations.NameValidator
import cz.kudladev.vehicletracking.core.domain.vehicles.validations.PlaceValidator
import cz.kudladev.vehicletracking.core.domain.vehicles.validations.SpzValidator
import cz.kudladev.vehicletracking.core.domain.vehicles.validations.YearValidator
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val validationModule = module {

    singleOf(::EmailValidation)
    singleOf(::FirstNameValidation)
    singleOf(::LastNameValidation)
    singleOf(::PasswordValidation)
    singleOf(::PhoneNumberValidation)
    singleOf(::PasswordConfirmValidation)

    singleOf(::BrandValidator)
    singleOf(::ColorValidator)
    singleOf(::FullNameValidator)
    singleOf(::MaximumDistanceValidator)
    singleOf(::ModelValidator)
    singleOf(::NameValidator)
    singleOf(::PlaceValidator)
    singleOf(::SpzValidator)
    singleOf(::YearValidator)

}