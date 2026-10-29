package cz.kudladev.vehicletracking.core.domain.auth

import cz.kudladev.vehicletracking.core.domain.auth.validations.EmailValidation
import cz.kudladev.vehicletracking.core.domain.auth.validations.FirstNameValidation
import cz.kudladev.vehicletracking.core.domain.auth.validations.LastNameValidation
import cz.kudladev.vehicletracking.core.domain.auth.validations.PasswordConfirmValidation
import cz.kudladev.vehicletracking.core.domain.auth.validations.PasswordValidation
import cz.kudladev.vehicletracking.core.domain.auth.validations.PhoneNumberValidation
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ValidationTest {

    private val firstNameValidation = FirstNameValidation()
    private val lastNameValidation = LastNameValidation()
    private val passwordValidation = PasswordValidation()
    private val passwordConfirmValidation = PasswordConfirmValidation()
    private val emailValidation = EmailValidation()
    private val phoneNumberValidation = PhoneNumberValidation()

    @Test
    fun firstNameValidation_enforcesServerBoundaries() {
        assertFalse(firstNameValidation.execute("").isSuccessful)
        assertFalse(firstNameValidation.execute("   ").isSuccessful)
        assertFalse(firstNameValidation.execute("Jan").isSuccessful) // 3 chars < 4
        assertTrue(firstNameValidation.execute("Jana").isSuccessful) // 4 chars min
        assertTrue(firstNameValidation.execute("Alexander").isSuccessful)
        assertTrue(firstNameValidation.execute("A".repeat(50)).isSuccessful) // 50 chars max
        assertFalse(firstNameValidation.execute("A".repeat(51)).isSuccessful) // 51 chars > 50
    }

    @Test
    fun lastNameValidation_enforcesServerBoundaries() {
        assertFalse(lastNameValidation.execute("").isSuccessful)
        assertFalse(lastNameValidation.execute("Nov").isSuccessful) // 3 chars < 4
        assertTrue(lastNameValidation.execute("Nova").isSuccessful) // 4 chars min
        assertTrue(lastNameValidation.execute("Novak").isSuccessful)
        assertTrue(lastNameValidation.execute("N".repeat(50)).isSuccessful)
        assertFalse(lastNameValidation.execute("N".repeat(51)).isSuccessful)
    }

    @Test
    fun passwordValidation_matchesServerRegexRules() {
        assertFalse(passwordValidation.execute("Pass1!").isSuccessful) // < 8 chars
        assertFalse(passwordValidation.execute("Password!").isSuccessful) // no digit
        assertFalse(passwordValidation.execute("PASSWORD1!").isSuccessful) // no lowercase
        assertFalse(passwordValidation.execute("password1!").isSuccessful) // no uppercase
        assertFalse(passwordValidation.execute("Password12").isSuccessful) // no special char
        assertFalse(passwordValidation.execute("Pass word1!").isSuccessful) // contains whitespace
        assertTrue(passwordValidation.execute("Password1!").isSuccessful) // valid
        assertTrue(passwordValidation.execute("P@ssw0rd!").isSuccessful) // valid
    }

    @Test
    fun emailValidation_acceptsStandardAddressesWithoutLiteralDollar() {
        assertFalse(emailValidation.execute("").isSuccessful)
        assertFalse(emailValidation.execute("notanemail").isSuccessful)
        assertFalse(emailValidation.execute("test@").isSuccessful)
        assertFalse(emailValidation.execute("@example.com").isSuccessful)
        assertTrue(emailValidation.execute("user@example.com").isSuccessful)
        assertTrue(emailValidation.execute("john.doe+tag@sub.domain.cz").isSuccessful)
    }

    @Test
    fun phoneNumberValidation_acceptsInternationalFormatsWithoutLiteralDollar() {
        assertFalse(phoneNumberValidation.execute("").isSuccessful)
        assertFalse(phoneNumberValidation.execute("12345").isSuccessful) // < 10
        assertFalse(phoneNumberValidation.execute("+123456789").isSuccessful) // < 10
        assertTrue(phoneNumberValidation.execute("+420123456789").isSuccessful)
        assertTrue(phoneNumberValidation.execute("0123456789").isSuccessful)
    }

    @Test
    fun passwordConfirmValidation_matchesEquality() {
        assertFalse(passwordConfirmValidation.execute("Password1!", "Password2!").isSuccessful)
        assertTrue(passwordConfirmValidation.execute("Password1!", "Password1!").isSuccessful)
    }
}
