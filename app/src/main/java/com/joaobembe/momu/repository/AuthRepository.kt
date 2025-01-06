package com.joaobembe.momu.repository

import android.util.Log
import com.joaobembe.momu.data.api.ApiService
import com.joaobembe.momu.data.api.TokenManager
import com.joaobembe.momu.data.models.UserResponse
import com.joaobembe.momu.data.models.LoginRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.os.Build

class AuthRepository(private val apiService: ApiService) {


    suspend fun login(email: String, password: String): String = withContext(Dispatchers.IO) {
        val response = apiService.api.login(LoginRequest(email, password, Build.MODEL))
        if (response.isSuccessful) {
            val token = response.body()?.accessToken
            if (token != null) {
                TokenManager.saveToken(token)
                return@withContext token
            } else {
                Log.d("AuthRepository", "Token não encontrado")
                throw Exception("Token não encontrado")
            }

        } else {
            Log.d("AuthRepository", "Erro ao fazer login: ${response.errorBody()?.string()}")
            throw Exception("Erro ao fazer login")
        }
    }

    suspend fun getUser(): UserResponse = withContext(Dispatchers.IO) {
        val response = apiService.api.getUser()
        if (response.isSuccessful) {
            response.body() ?: throw Exception("Usuário não encontrado")
        } else {
            throw Exception("Erro ao buscar dados do usuário")
        }
    }


    // Método para trocar o papel (role)
    suspend fun switchRole(role: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.api.switchRole(role)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Erro ao trocar de role"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
