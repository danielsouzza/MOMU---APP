package com.example.momu.data.models

data class AssessmentResponse(
    val id: Int,
    val id_evaluator: Int,
    val id_course: Int,
    val id_period: Int,
    val status: String,
    val evaluator: Evaluator,
    val course: Course
)

data class AssessmentGroupedResponse(
    val course_name: String,
    val period: Period,
    val assessments: List<AssessmentResponse>
)

data class Evaluator(
    val id: Int,
    val name: String,
    val image_url: String
)

data class Period(
    val id: Int,
    val date_start: String,
    val date_end: String,
    val semester: String,
    val open: Boolean,
)

data class Course(
    val id: Int,
    val name: String,
    val id_faculty: Int,
    val faculty: Faculty
)

data class Faculty(
    val id: Int,
    val name: String
)

data class AssessmentsResponse(
    val grouped: List<AssessmentGroupedResponse>,
    val ungrouped: List<AssessmentResponse>
)

