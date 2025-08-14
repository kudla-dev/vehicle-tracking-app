package cz.kudladev.vehicletracking.core.ui

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.stringResource
import vehicletracking.core.ui.generated.resources.Res
import vehicletracking.core.ui.generated.resources.back
import vehicletracking.core.ui.generated.resources.backView
import vehicletracking.core.ui.generated.resources.brand
import vehicletracking.core.ui.generated.resources.clear
import vehicletracking.core.ui.generated.resources.color
import vehicletracking.core.ui.generated.resources.confirmPasswordInfo
import vehicletracking.core.ui.generated.resources.`continue`
import vehicletracking.core.ui.generated.resources.driverLicense
import vehicletracking.core.ui.generated.resources.email
import vehicletracking.core.ui.generated.resources.emailExample
import vehicletracking.core.ui.generated.resources.error
import vehicletracking.core.ui.generated.resources.firstName
import vehicletracking.core.ui.generated.resources.firstNameExample
import vehicletracking.core.ui.generated.resources.frontView
import vehicletracking.core.ui.generated.resources.fullName
import vehicletracking.core.ui.generated.resources.lastName
import vehicletracking.core.ui.generated.resources.lastNameExample
import vehicletracking.core.ui.generated.resources.leftView
import vehicletracking.core.ui.generated.resources.licensePlate
import vehicletracking.core.ui.generated.resources.maximumDistance
import vehicletracking.core.ui.generated.resources.model
import vehicletracking.core.ui.generated.resources.next
import vehicletracking.core.ui.generated.resources.password
import vehicletracking.core.ui.generated.resources.phoneNumber
import vehicletracking.core.ui.generated.resources.phoneNumberExample
import vehicletracking.core.ui.generated.resources.place
import vehicletracking.core.ui.generated.resources.reject
import vehicletracking.core.ui.generated.resources.rightView
import vehicletracking.core.ui.generated.resources.search
import vehicletracking.core.ui.generated.resources.skip
import vehicletracking.core.ui.generated.resources.submit
import vehicletracking.core.ui.generated.resources.tachometerReading
import vehicletracking.core.ui.generated.resources.totalDistance
import vehicletracking.core.ui.generated.resources.year

@Composable
fun continueString(): String = stringResource(Res.string.`continue`)

@Composable
fun nextString(): String = stringResource(Res.string.next)

@Composable
fun backString(): String = stringResource(Res.string.back)

@Composable
fun skipString(): String = stringResource(Res.string.skip)

@Composable
fun submitString(): String = stringResource(Res.string.submit)

@Composable
fun rejectString(): String = stringResource(Res.string.reject)

@Composable
fun searchString(): String = stringResource(Res.string.search)

@Composable
fun cancelString(): String = stringResource(Res.string.clear)

@Composable
fun errorString(): String = stringResource(Res.string.error)

@Composable
fun clearString(): String = stringResource(Res.string.clear)

@Composable
fun firstNameString(): String = stringResource(Res.string.firstName)

@Composable
fun firstNameExampleString(): String = stringResource(Res.string.firstNameExample)

@Composable
fun lastNameString(): String = stringResource(Res.string.lastName)

@Composable
fun lastNameExampleString(): String = stringResource(Res.string.lastNameExample)

@Composable
fun emailString(): String = stringResource(Res.string.email)

@Composable
fun emailExampleString(): String = stringResource(Res.string.emailExample)

@Composable
fun passwordString(): String = stringResource(Res.string.password)

@Composable
fun confirmPasswordString(): String = stringResource(Res.string.confirmPasswordInfo)

@Composable
fun confirmPasswordInfoString(): String = stringResource(Res.string.confirmPasswordInfo)

@Composable
fun phoneNumberString(): String = stringResource(Res.string.phoneNumber)

@Composable
fun phoneNumberExampleString(): String = stringResource(Res.string.phoneNumberExample)

@Composable
fun frontViewString(): String = stringResource(Res.string.frontView)

@Composable
fun backViewString(): String = stringResource(Res.string.backView)

@Composable
fun leftViewString(): String = stringResource(Res.string.leftView)

@Composable
fun rightViewString(): String = stringResource(Res.string.rightView)

@Composable
fun tachometerReadingString(): String = stringResource(Res.string.tachometerReading)

@Composable
fun brandString(): String = stringResource(Res.string.brand)

@Composable
fun fullNameString(): String = stringResource(Res.string.fullName)

@Composable
fun modelString(): String = stringResource(Res.string.model)

@Composable
fun yearString(): String = stringResource(Res.string.year)

@Composable
fun colorString(): String = stringResource(Res.string.color)

@Composable
fun driverLicenseString(): String = stringResource(Res.string.driverLicense)

@Composable
fun licensePlateString(): String = stringResource(Res.string.licensePlate)

@Composable
fun totalDistanceString(): String = stringResource(Res.string.totalDistance)

@Composable
fun maximumDistanceString(): String = stringResource(Res.string.maximumDistance)

@Composable
fun placeString(): String = stringResource(Res.string.place)