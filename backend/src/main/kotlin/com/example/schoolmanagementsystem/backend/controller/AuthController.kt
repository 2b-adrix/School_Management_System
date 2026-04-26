package com.example.schoolmanagementsystem.backend.controller

import com.example.schoolmanagementsystem.backend.model.User
import com.example.schoolmanagementsystem.backend.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/signup")
    fun signUp(@RequestBody request: SignUpRequest): ResponseEntity<AuthResponse> {
        return ResponseEntity.ok(authService.signUp(request))
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<AuthResponse> {
        return ResponseEntity.ok(authService.login(request))
    }
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
