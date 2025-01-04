package com.example.momu.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.momu.data.api.ApiService
import com.example.momu.data.models.AssessmentGroupedResponse
import com.example.momu.data.models.AssessmentResponse
import com.example.momu.data.models.ResultResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class AssessmentViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _assessmentsState = MutableStateFlow(AssessmentsState())
    val assessmentsState: StateFlow<AssessmentsState> = _assessmentsState

    private val _assessmentDetailState = MutableStateFlow<AssessmentDetailState>(AssessmentDetailState.Loading)
    val assessmentDetailState: StateFlow<AssessmentDetailState> = _assessmentDetailState

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

    fun fetchResultAssessment(id: Int) {
        viewModelScope.launch {
            Log.d("AssessmentState", "Fetching details for ID: $id")
            _assessmentDetailState.value = AssessmentDetailState.Loading
            try {
                val response = apiService.api.getResultAssessment(id)
                if (response.isSuccessful) {
                    Log.d("AssessmentState", "Fetched Success for ID: $id")
                    _assessmentDetailState.value = AssessmentDetailState.Success(response.body())
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Erro desconhecido"
                    Log.d("AssessmentState", "Error for ID: $id - $errorMessage")
                    _assessmentDetailState.value = AssessmentDetailState.Error(errorMessage)
                }
            } catch (e: Exception) {
                Log.d("AssessmentState", "Exception for ID: $id - ${e.message}")
                _assessmentDetailState.value = AssessmentDetailState.Error("Erro: ${e.message}")
            }
        }
    }

}

sealed class AssessmentDetailState {
    object Loading : AssessmentDetailState()
    data class Success(val assessment: ResultResponse?) : AssessmentDetailState()
    data class Error(val message: String) : AssessmentDetailState()
}

data class AssessmentsState(
    val grouped: List<AssessmentGroupedResponse> = emptyList(),
    val ungrouped: List<AssessmentResponse> = emptyList(),
    val error: String? = null
)
