package ru.studyplanner.mobile.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.studyplanner.mobile.remote.CurrentUserDto
import ru.studyplanner.mobile.repository.AuthRepository
import ru.studyplanner.mobile.repository.ResultState

data class AuthUiState(
    val loading: Boolean = false,
    val user: CurrentUserDto? = null,
    val offline: Boolean = false,
    val error: String? = null
)

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val mutableState = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = mutableState.asStateFlow()

    init {
        if (authRepository.hasSavedSession()) {
            restoreSession()
        }
    }

    fun restoreSession() {
        viewModelScope.launch {
            mutableState.value = AuthUiState(loading = true)
            mutableState.value = when (val result = authRepository.restoreSession()) {
                is ResultState.Success -> AuthUiState(
                    user = result.data,
                    offline = result.fromCache
                )
                is ResultState.Error -> AuthUiState(error = result.message)
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            mutableState.value = AuthUiState(loading = true)
            mutableState.value = when (val result = authRepository.login(email, password)) {
                is ResultState.Success -> AuthUiState(user = result.data)
                is ResultState.Error -> AuthUiState(error = result.message)
            }
        }
    }

    fun register(email: String, password: String, fullName: String, groupName: String) {
        viewModelScope.launch {
            mutableState.value = AuthUiState(loading = true)
            mutableState.value = when (val result = authRepository.register(email, password, fullName, groupName)) {
                is ResultState.Success -> AuthUiState(user = result.data)
                is ResultState.Error -> AuthUiState(error = result.message)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            mutableState.value = AuthUiState()
        }
    }
}