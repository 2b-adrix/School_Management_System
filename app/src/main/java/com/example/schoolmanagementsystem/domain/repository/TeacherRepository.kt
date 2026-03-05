package com.example.schoolmanagementsystem.domain.repository

import com.example.schoolmanagementsystem.domain.model.Teacher
import com.example.schoolmanagementsystem.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface TeacherRepository {
    fun getAllTeachers(): Flow<Resource<List<Teacher>>>
    suspend fun getTeacherById(id: String): Resource<Teacher>
    suspend fun addTeacher(teacher: Teacher): Resource<Unit>
}
