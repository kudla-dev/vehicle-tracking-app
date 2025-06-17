package cz.kudladev.vehicletracking.core.domain.auth

import cz.kudladev.vehicletracking.model.ErrorMessage
import cz.kudladev.vehicletracking.model.User
import cz.kudladev.vehicletracking.model.Result

interface AuthRepository {

    suspend fun register(
        email: String,
        firstName: String,
        lastName: String,
        password: String,
        phoneNumber: String
    ): Result<Boolean, ErrorMessage>

    suspend fun login(
        email: String,
        password: String
    ): Result<Boolean, ErrorMessage>

    suspend fun refresh(
        refreshToken: String
    ): Result<Boolean, ErrorMessage>

    suspend fun auth(): Result<User, ErrorMessage>

}