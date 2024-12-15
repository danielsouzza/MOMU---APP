package com.example.momu.data.api

import android.content.Context
import android.content.SharedPreferences
import com.example.momu.App

object TokenManager {
    private const val PREF_NAME = "auth_pref"
    private const val TOKEN_KEY = "auth_token"

    private fun getPreferences(): SharedPreferences {
        return App.context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    // Salva o token
    fun saveToken(token: String) {
        val editor = getPreferences().edit()
        editor.putString(TOKEN_KEY, token)
        editor.apply()
    }

    // Recupera o token
    fun getToken(): String? {
        return getPreferences().getString(TOKEN_KEY, null)
    }

    // Limpa o token
    fun clearToken() {
        val editor = getPreferences().edit()
        editor.remove(TOKEN_KEY)
        editor.apply()
    }
}
