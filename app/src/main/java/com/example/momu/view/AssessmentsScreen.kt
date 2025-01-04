package com.example.momu.view

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.momu.viewmodel.AssessmentsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssessmentListScreen(
    navController: NavController,
    viewModel: AssessmentViewModel = hiltViewModel()
) {
    val assessmentsState by viewModel.assessmentsState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Avaliações")
                        },
//                navigationIcon = {
//                    IconButton(onClick = { navController.popBackStack() }) {
//                        Icon(
//                            imageVector = Icons.Default.ArrowBack,
//                            contentDescription = "Voltar"
//                        )
//                    }
//                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (assessmentsState) {

                is AssessmentsState.Error -> {
                    val errorMessage = (assessmentsState as AssessmentsState.Error).message
                    Text(
                        text = "Erro: $errorMessage",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is AssessmentsState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                is AssessmentsState.Success -> {
                    val successState = assessmentsState as AssessmentsState.Success
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        if (successState.ungrouped.isNotEmpty()) {
                            items(successState.ungrouped) { assessment ->
                                AssessmentCard(assessment) { selectedAssessment ->
                                    navController.navigate("result/${selectedAssessment.id}")
                                }
                            }
                        } else if (successState.grouped.isNotEmpty()) {
                            items(successState.grouped) { group ->
                                GroupedAssessmentCard(group, navController)
                            }
                        } else {
                            item {
                                Text(
                                    text = "Nenhuma avaliação disponível.",
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GroupedAssessmentCard(
    group: AssessmentGroupedResponse,
    navController: NavController
) {
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
            Text(
                text = "Curso: ${group.course_name}",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Semestre: ${group.period.semester} - ${group.period.date_start} até ${group.period.date_end}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            group.assessments.forEach { assessment ->
                AssessmentCard(
                    assessment,
                    onClick = { selectedAssessment ->
                        navController.navigate("result/${selectedAssessment.id}")
                    }
                )
            }
        }
    }
}

@Composable
fun AssessmentCard(
    assessment: AssessmentResponse,
    onClick: (AssessmentResponse) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick(assessment) },
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
