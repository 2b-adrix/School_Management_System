package com.example.schoolmanagementsystem.backend.domain.repository

import com.example.schoolmanagementsystem.backend.domain.model.Assignment
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface AssignmentRepository {
    fun getAllAssignments(): Flow<Resource<List<Assignment>>>
    suspend fun createAssignment(assignment: Assignment): Resource<Unit>
    suspend fun deleteAssignment(id: String): Resource<Unit>
    fun getAssignmentsForClass(classId: String): Flow<Resource<List<Assignment>>>
}

