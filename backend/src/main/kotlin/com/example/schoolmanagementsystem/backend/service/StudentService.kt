package com.example.schoolmanagementsystem.backend.service

import com.example.schoolmanagementsystem.backend.model.Student
import com.example.schoolmanagementsystem.backend.repository.StudentRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class StudentService(private val studentRepository: StudentRepository) {

    fun getCurrentStudentProfile(): Student {
        val email = SecurityContextHolder.getContext().authentication.name
        return studentRepository.findByUserEmail(email)
            ?: throw Exception("Student profile not found")
    }

    fun getAllStudents(): List<Student> {
        return studentRepository.findAll()
    }

    fun getStudentById(id: String): Student {
        return studentRepository.findById(id).orElseThrow { Exception("Student not found") }
    }

    fun saveStudent(student: Student): Student {
        return studentRepository.save(student)
    }

    fun updateStudent(id: String, student: Student): Student {
        if (!studentRepository.existsById(id)) {
            throw Exception("Student not found")
        }
        return studentRepository.save(student.copy(id = id))
    }

    fun deleteStudent(id: String) {
        studentRepository.deleteById(id)
    }
}
