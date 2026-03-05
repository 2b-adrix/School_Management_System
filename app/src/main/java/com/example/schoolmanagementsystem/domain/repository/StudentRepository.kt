package com.example.schoolmanagementsystem.domain.repository

import com.example.schoolmanagementsystem.domain.model.Student
import com.example.schoolmanagementsystem.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface StudentRepository {
    fun getAllStudents(): Flow<Resource<List<Student>>>
    suspend fun getStudentById(id: String): Resource<Student>
    suspend fun addStudent(student: Student): Resource<Unit>
    suspend fun updateStudent(student: Student): Resource<Unit>
    suspend fun deleteStudent(student: Student): Resource<Unit>
}
