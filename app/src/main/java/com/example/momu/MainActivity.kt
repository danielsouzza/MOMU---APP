package com.example.momu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.momu.data.api.ApiService
import com.example.momu.data.api.TokenManager
import com.example.momu.repository.AuthRepository
import com.example.momu.ui.theme.MOMUTheme
import com.example.momu.view.AssessmentListScreen
import com.example.momu.view.LoginScreen
import com.example.momu.view.RoleSelectionOrAssessmentListScreen
import com.example.momu.viewmodel.AssessmentViewModel
import com.example.momu.viewmodel.LoginViewModel
import com.example.momu.viewmodel.RoleViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MOMUTheme {
                val loginViewModel = LoginViewModel(AuthRepository(ApiService))
                val navController = rememberNavController()
                val startDestination = if (isUserLoggedIn()) "home" else "login"


                NavHost(navController = navController, startDestination = startDestination) {
                    composable("login") { LoginScreen(loginViewModel){

                    } }
                    composable("home") {
                        val roleViewModel = RoleViewModel(ApiService)
                        RoleSelectionOrAssessmentListScreen(navController,roleViewModel)
                    }
                    composable("assessmentList") {
                        val assessmentsViewModel = AssessmentViewModel(ApiService)
                        AssessmentListScreen(navController,assessmentsViewModel)
                    }
                }

            }
        }
    }
}

@Composable
fun isUserLoggedIn(): Boolean {
    val token = TokenManager.getToken()
    return !token.isNullOrEmpty()
}