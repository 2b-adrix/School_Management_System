package com.example.schoolmanagementsystem.backend.domain.repository

import com.example.schoolmanagementsystem.backend.domain.model.Student
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface StudentRepository {
    fun getAllStudents(): Flow<Resource<List<Student>>>
    suspend fun getStudentById(id: String): Resource<Student>
    suspend fun addStudent(student: Student): Resource<Unit>
    suspend fun bulkAddStudents(students: List<Student>): Resource<Unit>
    suspend fun updateStudent(student: Student): Resource<Unit>
    suspend fun deleteStudent(student: Student): Resource<Unit>
}

