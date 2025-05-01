package cz.kudladev.vehicletracking.auth.domain

import cz.kudladev.vehicletracking.network.DetailedDataError
import cz.kudladev.vehicletracking.network.Result

interface AuthRepository {

    suspend fun register(
        email: String,
        firstName: String,
        lastName: String,
        password: String,
        phoneNumber: String
    ): Result<Boolean, DetailedDataError>

    suspend fun login(
        email: String,
        password: String
    ): Result<Boolean, DetailedDataError>

    suspend fun refresh(
        refreshToken: String
    ): Result<Boolean, DetailedDataError>

}