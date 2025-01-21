// Arquivo: RoleSelectionScreen.kt
package com.joaobembe.momu.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.joaobembe.momu.R
import com.joaobembe.momu.viewmodel.RoleViewModel
import com.joaobembe.momu.viewmodel.RolesState

@Composable
fun RoleSelectionOrAssessmentListScreen(
    navController: NavController,
    viewModel: RoleViewModel = viewModel(),
) {
    val rolesState by viewModel.rolesState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFB3E1FF)),
        contentAlignment = Alignment.Center
    ) {
        BackgroundImageRole()

        if (rolesState.isLoading) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        } else {
            when {
                rolesState.roles.isNullOrEmpty() -> {
                    navController.navigate("login") {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                    }
                }
                rolesState.roles?.size == 1 -> {
                    rolesState.roles?.first().let { role ->
                        viewModel.switchRole(role!!.id)
                    }
                    navController.navigate("assessmentList")
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .imePadding()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        RoleSelectionCard(
                            rolesState = rolesState,
                            onRoleSelected = { selectedRoleId ->
                                viewModel.switchRole(selectedRoleId)
                                navController.navigate("assessmentList")
                            },
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BackgroundImageRole() {
    Image(
        painter = painterResource(id = R.drawable.bg_login),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun RoleSelectionCard(
    rolesState: RolesState,
    onRoleSelected: (Int) -> Unit,
    navController: NavController
) {
    Card(
        modifier = Modifier
            .width(500.dp)
            .height(500.dp)
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProfileIcon()

                Row (
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(
                        onClick = {
                            navController.navigate("login") {
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = true
                                }
                            }
                        },
                    ) {
                        Text("Sair")
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            rolesState.user?.let {
                Text(
                    text = it.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = it.email,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Selecionar perfil",
                style = MaterialTheme.typography.bodyMedium
            )

            rolesState.roles?.forEach { role ->
                RoleButton(role.name) {
                    onRoleSelected(role.id)
                }
            }
        }
    }
}


@Composable
fun ProfileIcon() {
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondary),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = Color.White
        )
    }
}

@Composable
fun RoleButton(roleName: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(roleName)
    }
}
