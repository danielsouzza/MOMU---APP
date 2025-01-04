package com.example.momu.repository

import com.example.momu.data.api.ApiService
import com.example.momu.data.api.TokenManager
import com.example.momu.data.models.UserResponse
import com.example.momu.data.models.LoginRequest
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
                throw Exception("Token não encontrado")
            }
        } else {
            throw Exception("Erro ao fazer login")
        }
    }

    suspend fun getUser(): UserResponse = withContext(Dispatchers.IO) {
        val response = apiService.api.getUser()
        (if (response.isSuccessful) {
            response.body()
        } else {
            throw Exception("Erro ao buscar dados do usuário")
        })!!
    }
}
