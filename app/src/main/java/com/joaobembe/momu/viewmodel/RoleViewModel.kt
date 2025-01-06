package com.joaobembe.momu.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joaobembe.momu.data.api.ApiService
import com.joaobembe.momu.data.models.Role
import com.joaobembe.momu.data.models.UserResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoleViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {



    private val _rolesState = MutableStateFlow(RolesState(isLoading = true))
    val rolesState: StateFlow<RolesState> = _rolesState



    init {
        fetchRoles()
    }

    private fun fetchRoles() {
        // Indicar que o carregamento começou
        _rolesState.value = RolesState(isLoading = true)

        viewModelScope.launch {
            try {
                val response = apiService.api.getUser()
                if (response.isSuccessful) {
                    val user = response.body()
                    _rolesState.value = RolesState(user = user, roles = user?.roles, isLoading = false)
                } else {
                    // Tratar erros da API
                    _rolesState.value = RolesState(
                        error = "Erro: ${response.code()} - ${response.message()}",
                        isLoading = true
                    )
                }
            } catch (e: Exception) {
                // Tratar exceções (ex.: falta de conexão)
                _rolesState.value = RolesState(error = "Erro ao buscar roles", isLoading = true)
                Log.e("FetchRoles", "Erro ao buscar roles", e)
            }
        }
    }


    fun switchRole(roleId: Int) {
        viewModelScope.launch {
            try {
                apiService.api.switchRole(roleId.toString())
            } catch (e: Exception) {
               Log.e("SwitchRole", "Erro ao trocar de role", e)
            }
        }
    }
}

data class RolesState(
    val user: UserResponse?=null,
    val roles: List<Role>? = emptyList(),
    val error: String? = null,
    val isLoading: Boolean
)

