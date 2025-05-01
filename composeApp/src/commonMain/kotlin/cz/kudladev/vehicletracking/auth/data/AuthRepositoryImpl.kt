package cz.kudladev.vehicletracking.auth.data

import cz.kudladev.vehicletracking.auth.domain.AuthRepository
import cz.kudladev.vehicletracking.network.DetailedDataError
import cz.kudladev.vehicletracking.network.Result

class AuthRepositoryImpl(
    private val authApi: AuthApi
): AuthRepository {
    override suspend fun register(
        email: String,
        firstName: String,
        lastName: String,
        password: String,
        phoneNumber: String
    ): Result<Boolean, DetailedDataError> {
        TODO("Not yet implemented")
    }

    override suspend fun login(
        email: String,
        password: String
    ): Result<Boolean, DetailedDataError> {
        TODO("Not yet implemented")
    }

    override suspend fun refresh(refreshToken: String): Result<Boolean, DetailedDataError> {
        TODO("Not yet implemented")
    }
}