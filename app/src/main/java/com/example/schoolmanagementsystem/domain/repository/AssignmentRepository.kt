package com.example.schoolmanagementsystem.domain.repository

import com.example.schoolmanagementsystem.domain.model.Assignment
import com.example.schoolmanagementsystem.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface AssignmentRepository {
    fun getAllAssignments(): Flow<Resource<List<Assignment>>>
    suspend fun createAssignment(assignment: Assignment): Resource<Unit>
    fun getAssignmentsForClass(classId: String): Flow<Resource<List<Assignment>>>
}
