package com.example.schoolmanagementsystem.backend.domain.repository

import com.example.schoolmanagementsystem.backend.domain.model.SchoolClass
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface ClassRepository {
    fun getAllClasses(): Flow<Resource<List<SchoolClass>>>
    suspend fun getClassById(id: String): Resource<SchoolClass>
    suspend fun addClass(schoolClass: SchoolClass): Resource<Unit>
    suspend fun updateClass(schoolClass: SchoolClass): Resource<Unit>
    suspend fun deleteClass(schoolClass: SchoolClass): Resource<Unit>
}

