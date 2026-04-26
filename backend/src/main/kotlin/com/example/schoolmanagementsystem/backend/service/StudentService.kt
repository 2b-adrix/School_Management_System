package com.example.schoolmanagementsystem.backend.service

import com.example.schoolmanagementsystem.backend.model.Student
import com.example.schoolmanagementsystem.backend.repository.StudentRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class StudentService(private val studentRepository: StudentRepository) {

    fun getCurrentStudentProfile(): Student {
        val email = SecurityContextHolder.getContext().authentication.name
        // In a real app, you might want to join with User to find by email
        // For simplicity, let's assume we have a findByUserEmail in StudentRepo
        return studentRepository.findAll().find { it.user.email == email }
            ?: throw Exception("Student profile not found")
    }

    fun getStudentById(id: String): Student {
        return studentRepository.findById(id).orElseThrow { Exception("Student not found") }
    }
}
