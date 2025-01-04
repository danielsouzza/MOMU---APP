package com.example.momu.data.models

data class ResultResponse(
    val id: Int,
    val course: String,
    val faculty: String,
    val evaluator: String,
    val chart: ChartData
)

data class ChartData(
    val labels: List<String>,
    val scores: List<Double>,
    val total: Int
)
