package com.example.momu.view

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.momu.data.models.AssessmentGroupedResponse
import com.example.momu.data.models.AssessmentResponse
import com.example.momu.viewmodel.AssessmentViewModel

@Composable
fun AssessmentListScreen(
    navController: NavController,
    viewModel: AssessmentViewModel = hiltViewModel()
) {
    val assessmentsState by viewModel.assessmentsState.collectAsState()

    IconButton(
        onClick = { navController.popBackStack() }, // Navega de volta à tela anterior
        modifier = Modifier.padding(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Voltar",
            tint = MaterialTheme.colorScheme.primary
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 40.dp)
    ) {
        when {
            assessmentsState.error != null -> {
                item {
                    Text(
                        text = "Erro: ${assessmentsState.error}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            assessmentsState.ungrouped.isNotEmpty() -> {

                items(assessmentsState.ungrouped) { assessment ->
                    AssessmentCard(assessment)
                }
            }
            assessmentsState.grouped.isNotEmpty() -> {

                items(assessmentsState.grouped) { group ->
                    GroupedAssessmentCard(group)
                }
            }
            else -> {
                item {
                    Text(
                        text = "Nenhuma avaliação disponível.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }


}

@Composable
fun GroupedAssessmentCard(group: AssessmentGroupedResponse) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.elevatedCardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header com nome do curso, semestre e bolinhas para avaliações
            Column(modifier = Modifier.fillMaxWidth()) {
                // Nome do curso
                Text(
                    text = "Curso: ${group.course_name}",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(end = 16.dp)
                )


                Spacer(modifier = Modifier.height(4.dp))

                // Informações sobre o semestre e as datas
                Text(
                    text = "Semestre: ${group.period.semester} - ${group.period.date_start} até ${group.period.date_end}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Loop para as avaliações, com cada avaliação sendo um card
            group.assessments.forEach { assessment ->
                AssessmentCard(assessment)
            }
        }
    }
}


@Composable
fun AssessmentCard(assessment: AssessmentResponse) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.elevatedCardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = "Status: ${assessment.status}",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Avaliador: ${assessment.evaluator.name}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
