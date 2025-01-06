package com.joaobembe.momu.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.joaobembe.momu.data.api.TokenManager
import com.joaobembe.momu.viewmodel.RoleViewModel
import com.joaobembe.momu.viewmodel.RolesState


@Composable
fun RoleSelectionOrAssessmentListScreen(
    navController: NavController,
    viewModel: RoleViewModel = viewModel(),
) {
    val rolesState by viewModel.rolesState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(16.dp), // Ajustando o padding geral
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (rolesState.roles?.isNotEmpty() == true) {
            if (rolesState.roles?.size == 1) {
                rolesState.roles?.first()?.let { viewModel.switchRole(it.id) }
                navController.navigate("assessmentList")
            } else {
                rolesState.let {
                    RoleSelectionCard(it) { selectedRoleId ->
                        viewModel.switchRole(selectedRoleId)
                        navController.navigate("assessmentList")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp)) // Espaço entre o card e o botão de voltar

        Button(
            onClick = {
                navController.navigate("login")
                TokenManager.clearToken()
                      },
            modifier = Modifier.align(Alignment.CenterHorizontally) // Centraliza o botão
        ) {
            Text("Voltar para Login")
        }
    }
}

@Composable
fun RoleSelectionCard(rolesState: RolesState, onRoleSelected: (Int) -> Unit) {

    Card(
        modifier = Modifier
            .fillMaxWidth(0.8f) // Define a largura do card para 80% da tela
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.Center),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            rolesState.user?.let {
                Text(
                    text = it.name, // Substitua pelo nome real
                    style = MaterialTheme.typography.titleMedium
                )
            }
            rolesState.user?.let {
                Text(
                    text = it.email, // Substitua pelo e-mail real
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Selecionar perfil",
                style = MaterialTheme.typography.bodyMedium
            )
            rolesState.roles?.forEach { role ->
                Button(
                    onClick = { onRoleSelected(role.id) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Text(role.name)
                }
            }
        }
    }
}