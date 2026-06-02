package ru.studyplanner.mobile.repository

import ru.studyplanner.mobile.remote.AuthApi
import ru.studyplanner.mobile.remote.CurrentUserDto
import ru.studyplanner.mobile.remote.LoginRequestDto
import ru.studyplanner.mobile.remote.RegisterRequestDto

class AuthRepository(
    private val authApi: AuthApi,
    private val tokenStore: TokenStore
) {
    fun hasSavedSession(): Boolean = tokenStore.hasToken()

    suspend fun restoreSession(): ResultState<CurrentUserDto> {
        if (!tokenStore.hasToken()) {
            return ResultState.Error("Нет сохраненной сессии")
        }
        val cachedUser = tokenStore.cachedUser
        return try {
            val remoteUser = authApi.me()
            tokenStore.saveUser(remoteUser)
            ResultState.Success(remoteUser)
        } catch (exception: Exception) {
            if (cachedUser == null) {
                ResultState.Error(exception.toUserMessage("Не удалось восстановить сессию"))
            } else {
                ResultState.Success(cachedUser, fromCache = true)
            }
        }
    }

    suspend fun login(email: String, password: String): ResultState<CurrentUserDto> {
        return try {
            val response = authApi.login(LoginRequestDto(email, password))
            tokenStore.save(response.accessToken)
            val currentUser = authApi.me()
            tokenStore.saveSession(response.accessToken, currentUser)
            ResultState.Success(currentUser)
        } catch (exception: Exception) {
            ResultState.Error(exception.toUserMessage("Не удалось войти"))
        }
    }

    suspend fun register(
        email: String,
        password: String,
        fullName: String,
        groupName: String
    ): ResultState<CurrentUserDto> {
        return try {
            val response = authApi.register(RegisterRequestDto(email, password, fullName, groupName))
            tokenStore.save(response.accessToken)
            val currentUser = authApi.me()
            tokenStore.saveSession(response.accessToken, currentUser)
            ResultState.Success(currentUser)
        } catch (exception: Exception) {
            ResultState.Error(exception.toUserMessage("Не удалось зарегистрироваться"))
        }
    }

    suspend fun logout() {
        tokenStore.clear()
    }
}