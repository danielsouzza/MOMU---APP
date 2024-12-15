package com.example.momu.data.models

data class ConsolidatedResultsResponse(
    val legend: String,
    val data: List<DimensionResult>,
    val total: Double
)
