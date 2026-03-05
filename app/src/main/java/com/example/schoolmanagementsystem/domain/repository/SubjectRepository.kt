package com.example.schoolmanagementsystem.domain.repository

import com.example.schoolmanagementsystem.domain.model.Subject
import com.example.schoolmanagementsystem.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface SubjectRepository {
    fun getAllSubjects(): Flow<Resource<List<Subject>>>
    suspend fun getSubjectById(id: String): Resource<Subject>
    suspend fun addSubject(subject: Subject): Resource<Unit>
}
