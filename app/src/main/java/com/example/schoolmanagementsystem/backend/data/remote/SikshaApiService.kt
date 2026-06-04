package com.example.schoolmanagementsystem.backend.data.remote

import com.example.schoolmanagementsystem.backend.domain.model.*
import retrofit2.http.*

interface SikshaApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<AuthResponse>

    @POST("auth/signup")
    suspend fun signUp(@Body request: SignUpRequest): ApiResponse<AuthResponse>

    @GET("students/profile")
    suspend fun getStudentProfile(): ApiResponse<Student>

    @GET("students")
    suspend fun getAllStudents(): ApiResponse<List<Student>>

    @GET("students/{id}")
    suspend fun getStudentById(@Path("id") id: String): ApiResponse<Student>

    @POST("students")
    suspend fun addStudent(@Body student: Student): ApiResponse<Student>

    @PUT("students/{id}")
    suspend fun updateStudent(@Path("id") id: String, @Body student: Student): ApiResponse<Student>

    @DELETE("students/{id}")
    suspend fun deleteStudent(@Path("id") id: String): ApiResponse<Unit>

    // Add other endpoints as needed
}

data class LoginRequest(val email: String, val password: String)
data class SignUpRequest(
    val email: String, 
    val password: String, 
    val fullName: String, 
    val role: String, 
    val schoolId: String
)
data class AuthResponse(val token: String, val user: User)
