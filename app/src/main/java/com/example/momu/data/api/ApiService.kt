package com.example.momu.data.api

import com.example.momu.data.models.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Interceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

object ApiService {
    private const val BASE_URL = "https://momu.com.br/api/"

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->

            val token = TokenManager.getToken()
            val request: Request = chain.request().newBuilder()
                .apply {
                    token?.let {
                        header("Authorization", "Bearer $it")
                    }
                }
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build()

            chain.proceed(request)
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: ApiEndpoints = retrofit.create(ApiEndpoints::class.java)
}

interface ApiEndpoints {

    @POST("auth")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("user")
    suspend fun getUser(): Response<UserResponse>

    @GET("assessments")
    suspend fun getAssessments(): Response<AssessmentsResponse>

    @GET("assessments/{id}/result")
    suspend fun getResultAssessment(@Path("id") id: Int): Response<ResultResponse>

    @GET("assessments/{id}/answers")
    suspend fun getAnswersAssessment(@Path("id") id: Int): Response<List<AnswerResponse>>

    @GET("assessments/course/{course_id}/period/{period_id}/result")
    suspend fun getConsolidatedResults(
        @Path("course_id") courseId: Int,
        @Path("period_id") periodId: Int
    ): Response<ConsolidatedResultsResponse>

    @POST("switch-role/{role}")
    suspend fun switchRole(@Path("role") role: String): Response<SwitchRoleResponse>
}
