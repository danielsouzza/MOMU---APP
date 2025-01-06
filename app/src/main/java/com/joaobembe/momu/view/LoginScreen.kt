package com.joaobembe.momu.view

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.joaobembe.momu.viewmodel.LoginUiState
import com.joaobembe.momu.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onLoginSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (uiState) {
            is LoginUiState.Idle -> LoginForm { email, password ->
                viewModel.login(email, password)
            }

            is LoginUiState.Loading -> CircularProgressIndicator()

            is LoginUiState.Success -> {
                onLoginSuccess()
            }

            is LoginUiState.Error -> {
                val message = (uiState as LoginUiState.Error).message
                Text(text = "Erro: $message", color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(16.dp))
                LoginForm { email, password ->
                    viewModel.login(email, password)
                }
            }
        }
    }
}

@Composable
fun LoginForm(onLogin: (String, String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    OutlinedTextField(
        value = email,
        onValueChange = { email = it },
        label = { Text("Email") },
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
        value = password,
        onValueChange = { password = it },
        label = { Text("Senha") },
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = PasswordVisualTransformation()
    )

    Spacer(modifier = Modifier.height(16.dp))

    Button(
        onClick = { onLogin(email, password) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Entrar")
    }
}