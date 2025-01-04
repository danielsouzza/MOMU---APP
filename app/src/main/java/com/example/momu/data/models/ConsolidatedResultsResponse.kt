package com.example.momu.data.models

data class ConsolidatedResultsResponse(
    val course: String,
    val faculty: String,
    val period: String,
    val assessments: List<AssessmentsWithEvaluators>,
    val chart: ChartData
)

data class AssessmentsWithEvaluators(
    val assessment_id: Int,
    val evaluator: List<Evaluator>
)

