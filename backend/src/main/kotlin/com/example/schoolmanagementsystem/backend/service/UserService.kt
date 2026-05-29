package com.example.schoolmanagementsystem.backend.service

import com.example.schoolmanagementsystem.backend.controller.UpdateProfileRequest
import com.example.schoolmanagementsystem.backend.exception.ResourceNotFoundException
import com.example.schoolmanagementsystem.backend.model.User
import com.example.schoolmanagementsystem.backend.model.UserRole
import com.example.schoolmanagementsystem.backend.repository.StudentRepository
import com.example.schoolmanagementsystem.backend.repository.TeacherRepository
import com.example.schoolmanagementsystem.backend.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val studentRepository: StudentRepository,
    private val teacherRepository: TeacherRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun getFullProfile(email: String): Any {
        val user = userRepository.findByEmail(email)
            ?: throw ResourceNotFoundException("User not found")
        
        return when (user.role) {
            UserRole.STUDENT -> studentRepository.findByUserEmail(email) ?: user
            UserRole.TEACHER -> teacherRepository.findByUserEmail(email) ?: user
            else -> user
        }
    }

    @Transactional
    fun changePassword(email: String, oldPass: String, newPass: String) {
        val user = userRepository.findByEmail(email)
            ?: throw ResourceNotFoundException("User not found")
        
        if (!passwordEncoder.matches(oldPass, user.password)) {
            throw Exception("Current password does not match")
        }
        
        val updatedUser = user.copy(passwordHash = passwordEncoder.encode(newPass))
        userRepository.save(updatedUser)
    }

    @Transactional
    fun updateProfile(email: String, request: UpdateProfileRequest): User {
        val user = userRepository.findByEmail(email)
            ?: throw ResourceNotFoundException("User not found")
        
        val updatedUser = user.copy(name = request.name ?: user.name)
        val savedUser = userRepository.save(updatedUser)
        
        // Update linked profile if exists
        when (user.role) {
            UserRole.STUDENT -> {
                studentRepository.findByUserEmail(email)?.let { student ->
                    val updatedStudent = student.copy(
                        parentContact = request.phoneNumber ?: student.parentContact,
                        photoUrl = request.photoUrl ?: student.photoUrl
                    )
                    studentRepository.save(updatedStudent)
                }
            }
            UserRole.TEACHER -> {
                teacherRepository.findByUserEmail(email)?.let { teacher ->
                    val updatedTeacher = teacher.copy(
                        phoneNumber = request.phoneNumber ?: teacher.phoneNumber,
                        qualification = request.qualification ?: teacher.qualification,
                        profileImageUrl = request.photoUrl ?: teacher.profileImageUrl
                    )
                    teacherRepository.save(updatedTeacher)
                }
            }
            else -> {}
        }
        
        return savedUser
    }
}
