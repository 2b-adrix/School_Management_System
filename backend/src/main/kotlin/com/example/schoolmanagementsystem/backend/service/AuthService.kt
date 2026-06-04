package com.example.schoolmanagementsystem.backend.service

import com.example.schoolmanagementsystem.backend.controller.LoginRequest
import com.example.schoolmanagementsystem.backend.controller.SignUpRequest
import com.example.schoolmanagementsystem.backend.dto.AuthResponse
import com.example.schoolmanagementsystem.backend.dto.UserDto
import com.example.schoolmanagementsystem.backend.model.Student
import com.example.schoolmanagementsystem.backend.model.Teacher
import com.example.schoolmanagementsystem.backend.model.User
import com.example.schoolmanagementsystem.backend.model.UserRole
import com.example.schoolmanagementsystem.backend.repository.StudentRepository
import com.example.schoolmanagementsystem.backend.repository.TeacherRepository
import com.example.schoolmanagementsystem.backend.repository.UserRepository
import com.example.schoolmanagementsystem.backend.security.JwtService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val studentRepository: StudentRepository,
    private val teacherRepository: TeacherRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager
) {

    @Transactional
    fun signUp(request: SignUpRequest): AuthResponse {
        if (userRepository.findByEmail(request.email) != null) {
            throw Exception("User with email ${request.email} already exists")
        }

        val user = User(
            name = request.fullName,
            email = request.email,
            passwordHash = passwordEncoder.encode(request.password),
            role = UserRole.valueOf(request.role),
            schoolId = request.schoolId
        )
        val savedUser = userRepository.save(user)
        
        // Create linked profile
        when (savedUser.role) {
            UserRole.STUDENT -> {
                studentRepository.save(Student(
                    id = savedUser.id!!,
                    user = savedUser,
                    rollNumber = "TEMP-${System.currentTimeMillis()}", // Admin can update later
                    attendancePercentage = 0f
                ))
            }
            UserRole.TEACHER -> {
                teacherRepository.save(Teacher(
                    id = savedUser.id!!,
                    user = savedUser,
                    phoneNumber = "N/A",
                    qualification = "N/A"
                ))
            }
            else -> {}
        }

        val jwtToken = jwtService.generateToken(savedUser)
        return AuthResponse(
            token = jwtToken,
            user = savedUser.toDto()
        )
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
        return AuthResponse(
            token = jwtToken,
            user = user.toDto()
        )
    }

    private fun User.toDto() = UserDto(
        id = id,
        name = name,
        email = email,
        role = role,
        schoolId = schoolId
    )
}
