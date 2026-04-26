package com.example.schoolmanagementsystem.backend.service

import com.example.schoolmanagementsystem.backend.controller.AuthResponse
import com.example.schoolmanagementsystem.backend.controller.LoginRequest
import com.example.schoolmanagementsystem.backend.controller.SignUpRequest
import com.example.schoolmanagementsystem.backend.model.User
import com.example.schoolmanagementsystem.backend.model.UserRole
import com.example.schoolmanagementsystem.backend.repository.UserRepository
import com.example.schoolmanagementsystem.backend.security.JwtService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager
) {

    fun signUp(request: SignUpRequest): AuthResponse {
        val user = User(
            name = request.fullName,
            email = request.email,
            passwordHash = passwordEncoder.encode(request.password),
            role = UserRole.valueOf(request.role),
            schoolId = request.schoolId
        )
        val savedUser = userRepository.save(user)
        val jwtToken = jwtService.generateToken(savedUser)
        return AuthResponse(token = jwtToken, user = savedUser)
    }

    fun login(request: LoginRequest): AuthResponse {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                request.email,
                request.password
            )
        )
        val user = userRepository.findByEmail(request.email)
            ?: throw Exception("User not found")
        val jwtToken = jwtService.generateToken(user)
        return AuthResponse(token = jwtToken, user = user)
    }
}
