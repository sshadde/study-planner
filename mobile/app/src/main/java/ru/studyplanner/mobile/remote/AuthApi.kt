package ru.studyplanner.mobile.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequestDto): AuthResponseDto

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequestDto): AuthResponseDto

    @GET("api/auth/me")
    suspend fun me(): CurrentUserDto
}

data class LoginRequestDto(
    val email: String,
    val password: String
)

data class RegisterRequestDto(
    val email: String,
    val password: String,
    val fullName: String,
    val groupName: String
)

data class AuthResponseDto(
    val accessToken: String,
    val userId: Long,
    val email: String,
    val role: String,
    val fullName: String,
    val groupName: String
)

data class CurrentUserDto(
    val id: Long,
    val email: String,
    val role: String,
    val fullName: String,
    val groupName: String
)
