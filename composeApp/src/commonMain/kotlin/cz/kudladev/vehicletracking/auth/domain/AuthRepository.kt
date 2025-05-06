package cz.kudladev.vehicletracking.auth.domain

import cz.kudladev.vehicletracking.auth.data.models.TokenPair
import cz.kudladev.vehicletracking.auth.data.models.UserResponse
import cz.kudladev.vehicletracking.network.ErrorMessage
import cz.kudladev.vehicletracking.network.Result

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
    ): Result<TokenPair, ErrorMessage>

    suspend fun refresh(
        refreshToken: String
    ): Result<TokenPair, ErrorMessage>

    suspend fun auth(): Result<User, ErrorMessage>

}