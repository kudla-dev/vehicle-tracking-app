package cz.kudladev.vehicletracking.auth.data

import cz.kudladev.vehicletracking.auth.data.models.LoginRequest
import cz.kudladev.vehicletracking.auth.data.models.RefreshRequest
import cz.kudladev.vehicletracking.auth.data.models.RegisterRequest
import cz.kudladev.vehicletracking.auth.data.models.TokenPair
import cz.kudladev.vehicletracking.auth.data.models.UserResponse
import cz.kudladev.vehicletracking.auth.domain.AuthRepository
import cz.kudladev.vehicletracking.auth.domain.User
import cz.kudladev.vehicletracking.auth.domain.toUser
import cz.kudladev.vehicletracking.network.ErrorMessage
import cz.kudladev.vehicletracking.network.Result
import cz.kudladev.vehicletracking.network.safeCall
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class AuthRepositoryImpl(
    private val httpClient: HttpClient
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
    ): Result<TokenPair, ErrorMessage> {
        val loginRequest = LoginRequest(
            email = email,
            password = password
        )
        return safeCall<TokenPair> {
            httpClient.post("/auth/login") {
                setBody(loginRequest)
            }
        }
    }

    override suspend fun refresh(refreshToken: String): Result<TokenPair, ErrorMessage> {
        val refreshRequest = RefreshRequest(
            refreshToken = refreshToken
        )
        return safeCall<TokenPair> {
            httpClient.post("/auth/refresh") {
                setBody(refreshRequest)
            }
        }
    }

    override suspend fun auth(): Result<User, ErrorMessage> {
        val response = safeCall<UserResponse> {
            httpClient.post("/auth")
        }

        return when (response) {
            is Result.Success -> {
                Result.Success(response.data.toUser())
            }
            is Result.Error -> {
                Result.Error(response.error)
            }
        }
    }
}