package com.example.schoolmanagementsystem.backend.domain.repository

import com.example.schoolmanagementsystem.backend.domain.model.Teacher
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface TeacherRepository {
    fun getAllTeachers(): Flow<Resource<List<Teacher>>>
    suspend fun getTeacherById(id: String): Resource<Teacher>
    suspend fun addTeacher(teacher: Teacher): Resource<Unit>
    suspend fun updateTeacher(teacher: Teacher): Resource<Unit>
    suspend fun deleteTeacher(teacher: Teacher): Resource<Unit>
}

