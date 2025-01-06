package com.joaobembe.momu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joaobembe.momu.data.models.UserResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.joaobembe.momu.repository.AuthRepository


class LoginViewModel(private val repository: AuthRepository) : ViewModel() {


    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState


    // Fazer login
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {
                repository.login(email, password)
                val user = repository.getUser()
                _uiState.value = LoginUiState.Success( user)
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }

    fun user() {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {
                val user = repository.getUser()
                _uiState.value = LoginUiState.Success(user)
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }
}

// Estados possíveis da tela de login
sealed class LoginUiState {
    data object Idle : LoginUiState()
    data object Loading : LoginUiState()
    data class Success(val user: UserResponse) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}