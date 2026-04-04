package com.example.schoolmanagementsystem.backend.domain.repository

import com.example.schoolmanagementsystem.backend.domain.model.Subject
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface SubjectRepository {
    fun getAllSubjects(): Flow<Resource<List<Subject>>>
    suspend fun getSubjectById(id: String): Resource<Subject>
    suspend fun addSubject(subject: Subject): Resource<Unit>
    suspend fun updateSubject(subject: Subject): Resource<Unit>
    suspend fun deleteSubject(subject: Subject): Resource<Unit>
}

