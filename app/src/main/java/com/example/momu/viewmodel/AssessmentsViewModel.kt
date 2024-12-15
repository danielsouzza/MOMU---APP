package com.example.momu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.momu.data.api.ApiService
import com.example.momu.data.models.AssessmentGroupedResponse
import com.example.momu.data.models.AssessmentResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssessmentViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _assessmentsState = MutableStateFlow(AssessmentsState())
    val assessmentsState: StateFlow<AssessmentsState> = _assessmentsState

    init {
        fetchAssessments()
    }


    private fun fetchAssessments() {
        viewModelScope.launch {
            try {
                val response = apiService.api.getAssessments()
                if (response.isSuccessful) {
                    val data = response.body()
                    _assessmentsState.value = AssessmentsState(
                        grouped = data?.grouped ?: emptyList(),
                        ungrouped = data?.ungrouped ?: emptyList()
                    )
                } else {
                    _assessmentsState.value = AssessmentsState(error = "Erro ao buscar avaliações")
                }
            } catch (e: Exception) {
                _assessmentsState.value = AssessmentsState(error = "Erro ao conectar")
            }
        }
    }

}

data class AssessmentsState(
    val grouped: List<AssessmentGroupedResponse> = emptyList(),
    val ungrouped: List<AssessmentResponse> = emptyList(),
    val error: String? = null
)
