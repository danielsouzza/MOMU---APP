package com.joaobembe.momu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.joaobembe.momu.data.api.ApiService
import com.joaobembe.momu.data.api.TokenManager
import com.joaobembe.momu.repository.AuthRepository
import com.joaobembe.momu.ui.theme.MOMUTheme
import com.joaobembe.momu.view.AssessmentListScreen
import com.joaobembe.momu.view.LoginScreen
import com.joaobembe.momu.view.ResultScreen
import com.joaobembe.momu.view.RoleSelectionOrAssessmentListScreen
import com.joaobembe.momu.viewmodel.AssessmentViewModel
import com.joaobembe.momu.viewmodel.LoginViewModel
import com.joaobembe.momu.viewmodel.RoleViewModel

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
                    composable("login") {
                        LoginScreen(loginViewModel, onLoginSuccess = {
                            navController.navigate("home")
                        })
                    }
                    composable("home") {
                        val roleViewModel = RoleViewModel(ApiService)
                        RoleSelectionOrAssessmentListScreen(navController, roleViewModel)
                    }
                    composable("assessmentList") {
                        val assessmentsViewModel = AssessmentViewModel(ApiService)
                        AssessmentListScreen(navController, assessmentsViewModel)
                    }
                    composable(
                        route = "result/{assessmentId}",
                        arguments = listOf(navArgument("assessmentId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val assessmentId = backStackEntry.arguments?.getInt("assessmentId") ?: -1
                        val assessmentsViewModel = AssessmentViewModel(ApiService)
                        ResultScreen(navController, assessmentId, assessmentsViewModel)
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