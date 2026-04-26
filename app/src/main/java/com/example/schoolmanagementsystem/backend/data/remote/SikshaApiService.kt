package com.example.schoolmanagementsystem.backend.data.remote

import com.example.schoolmanagementsystem.backend.domain.model.*
import retrofit2.http.*

interface SikshaApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("auth/signup")
    suspend fun signUp(@Body request: SignUpRequest): AuthResponse

    @GET("students/profile")
    suspend fun getStudentProfile(): Student

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
