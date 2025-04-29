package com.tamersarioglu.veroandroidtask.presentation.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tamersarioglu.veroandroidtask.domain.usecase.LoginUseCase
import com.tamersarioglu.veroandroidtask.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Initial)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            val result = loginUseCase(username, password)

            when (result) {
                is Resource.Success -> {
                    Log.d("LoginViewModel", "Login successful: ${result.data}")
                    _loginState.value = LoginState.Success
                }
                is Resource.Error -> {
                    Log.e("LoginViewModel", "Login failed: ${result.message}")
                    _loginState.value = LoginState.Error(result.message ?: "Unknown error")
                }
                is Resource.Loading -> {
                    _loginState.value = LoginState.Loading
                }
            }
        }
    }
}

sealed class LoginState {
    object Initial : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}