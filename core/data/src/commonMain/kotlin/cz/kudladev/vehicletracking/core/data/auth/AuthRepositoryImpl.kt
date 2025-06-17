package cz.kudladev.vehicletracking.core.data.auth

import cz.kudladev.vehicletracking.core.data.auth.models.LoginRequest
import cz.kudladev.vehicletracking.core.data.auth.models.RefreshRequest
import cz.kudladev.vehicletracking.core.data.auth.models.RegisterRequest
import cz.kudladev.vehicletracking.core.data.auth.models.TokenPair
import cz.kudladev.vehicletracking.core.data.auth.models.UserResponse
import cz.kudladev.vehicletracking.core.data.auth.models.toDomain
import cz.kudladev.vehicletracking.core.domain.auth.AuthRepository
import cz.kudladev.vehicletracking.core.domain.auth.UserStateHolder
import cz.kudladev.vehicletracking.model.User
import cz.kudladev.vehicletracking.model.ErrorMessage
import cz.kudladev.vehicletracking.model.Result
import cz.kudladev.vehicletracking.network.safeCall
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class AuthRepositoryImpl(
    private val httpClient: HttpClient,
    private val userStateHolder: UserStateHolder
): AuthRepository {
    override suspend fun register(
        email: String,
        firstName: String,
        lastName: String,
        password: String,
        phoneNumber: String
    ): Result<Boolean, ErrorMessage> {
        val registerRequest = RegisterRequest(
            email = email,
            firstName = firstName,
            lastName = lastName,
            password = password,
            phoneNumber = phoneNumber
        )
        return safeCall<Boolean>{
            httpClient.post("/auth/register") {
                setBody(registerRequest)
            }
        }
    }

    override suspend fun login(
        email: String,
        password: String
    ): Result<Boolean, ErrorMessage> {
        val loginRequest = LoginRequest(
            email = email,
            password = password
        )
        val result = safeCall<TokenPair> {
            httpClient.post("/auth/login") {
                setBody(loginRequest)
            }
        }

        return tokenResultHandling(result)
    }



    override suspend fun refresh(refreshToken: String): Result<Boolean, ErrorMessage> {
        val refreshRequest = RefreshRequest(
            refreshToken = refreshToken
        )
        val result = safeCall<TokenPair> {
            httpClient.post("/auth/refresh") {
                setBody(refreshRequest)
            }
        }

        return tokenResultHandling(result)
    }

    override suspend fun auth(): Result<User, ErrorMessage> {
        val response = safeCall<UserResponse> {
            httpClient.post("/auth")
        }

        return when (response) {
            is Result.Success -> {
                Result.Success(response.data.toDomain())
            }
            is Result.Error -> {
                Result.Error(response.error)
            }
            else -> {
                Result.Loading
            }
        }
    }

    private suspend fun tokenResultHandling(result: Result<TokenPair, ErrorMessage>): Result<Boolean, ErrorMessage> {
        return when (result) {
            is Result.Success -> {
                userStateHolder.updateTokens(
                    accessToken = result.data.accessToken,
                    refreshToken = result.data.refreshToken
                )
                Result.Success(true)
            }

            is Result.Error -> {
                Result.Error(result.error)
            }

            else -> {
                Result.Loading
            }
        }
    }
}