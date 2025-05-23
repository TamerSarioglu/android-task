package com.tamersarioglu.veroandroidtask.presentation.auth

import androidx.lifecycle.ViewModel
import com.tamersarioglu.veroandroidtask.domain.usecase.IsAuthenticatedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val isAuthenticatedUseCase: IsAuthenticatedUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        checkAuthenticationState()
    }

    private fun checkAuthenticationState() {
        val isAuthenticated = isAuthenticatedUseCase()
        _authState.value = if (isAuthenticated) {
            AuthState.Authenticated
        } else {
            AuthState.Unauthenticated
        }
    }

    fun onLoginSuccess() {
        _authState.value = AuthState.Authenticated
    }

    fun onLogout() {
        _authState.value = AuthState.Unauthenticated
    }
}

sealed class AuthState {
    object Loading : AuthState()
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
} 