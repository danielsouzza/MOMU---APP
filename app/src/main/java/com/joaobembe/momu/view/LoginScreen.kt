package com.joaobembe.momu.view

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.joaobembe.momu.App.Companion.context
import com.joaobembe.momu.R
import com.joaobembe.momu.viewmodel.LoginUiState
import com.joaobembe.momu.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFB3E1FF))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        BackgroundImage()
        LoginCard(uiState, onLogin = { email, password ->
            viewModel.login(email, password)
        }, onLoginSuccess = onLoginSuccess)
    }
}

@Composable
fun BackgroundImage() {
    Image(
        painter = painterResource(id = R.drawable.bg_login),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun LoginCard(
    uiState: LoginUiState,
    onLogin: (String, String) -> Unit,
    onLoginSuccess: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(500.dp)
            .height(500.dp)
    ) {}

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (uiState) {
            is LoginUiState.Idle -> LoginForm(onLogin)
            is LoginUiState.Loading -> CircularProgressIndicator()
            is LoginUiState.Success -> onLoginSuccess()
            is LoginUiState.Error -> {
                ErrorMessage((uiState as LoginUiState.Error).message)
                Spacer(modifier = Modifier.height(16.dp))
                LoginForm(onLogin)
            }
        }
    }
}

@Composable
fun ErrorMessage(message: String) {
    Text(
        text = "Erro: $message",
        color = MaterialTheme.colorScheme.error
    )
}

@Composable
fun LoginForm(onLogin: (String, String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val (rememberMe, setRememberMe) = remember { mutableStateOf(false) }

    LogoImage()
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        modifier = Modifier.padding(bottom = 16.dp),
        style = MaterialTheme.typography.displayMedium,
        color = Color.DarkGray,
        text = "Login"
    )

    EmailField(email) { email = it }
    Spacer(modifier = Modifier.height(8.dp))
    PasswordField(password) { password = it }
    Spacer(modifier = Modifier.height(16.dp))
    RememberMeCheckbox(rememberMe, setRememberMe)
    Spacer(modifier = Modifier.height(16.dp))
    LoginActions(email, password, onLogin)
}

@Composable
fun LogoImage() {
    Image(
        painter = painterResource(id = R.drawable.fieg_blue_logo),
        contentDescription = null,
        modifier = Modifier
            .width(200.dp)
            .wrapContentSize()
    )
}

@Composable
fun EmailField(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Email") },
        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(FocusRequester()),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = androidx.compose.ui.text.input.ImeAction.Next
        )
    )
}

@Composable
fun PasswordField(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Senha") },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = PasswordVisualTransformation(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = androidx.compose.ui.text.input.ImeAction.Done
        )
    )
}

@Composable
fun RememberMeCheckbox(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        Text(
            text = "Lembrar de mim",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Composable
fun LoginActions(email: String, password: String, onLogin: (String, String) -> Unit) {
    val context = LocalContext.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ForgotPasswordButton(context)
        LoginButton(email, password, onLogin)
    }
}

@Composable
fun ForgotPasswordButton(context: android.content.Context) {
    TextButton(
        onClick = {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://momu.com.br/forgot-password")).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    ) {
        Text("Esqueceu a senha?")
    }
}

@Composable
fun LoginButton(email: String, password: String, onLogin: (String, String) -> Unit) {
    Button(onClick = { onLogin(email, password) }) {
        Text("Entrar")
    }
}