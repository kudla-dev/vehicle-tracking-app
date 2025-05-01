package cz.kudladev.vehicletracking.auth.data

import cz.kudladev.vehicletracking.auth.data.models.LoginRequest
import cz.kudladev.vehicletracking.auth.data.models.RegisterRequest
import cz.kudladev.vehicletracking.auth.data.models.TokenPair
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST

interface AuthApi {

    @POST("/auth/register")
    suspend fun register(
        @Body registerRequest: RegisterRequest
    )

    @POST("/auth/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): TokenPair

    suspend fun refresh(
        @Body refreshToken: String
    ): TokenPair

}