package com.example.schoolmanagementsystem.data.repository

import com.example.schoolmanagementsystem.domain.model.Assignment
import com.example.schoolmanagementsystem.domain.repository.AssignmentRepository
import com.example.schoolmanagementsystem.domain.util.Resource
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AssignmentRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest
) : AssignmentRepository {

    override fun getAllAssignments(): Flow<Resource<List<Assignment>>> = flow {
        emit(Resource.Loading())
        try {
            val assignments = postgrest["assignments"]
                .select()
                .decodeList<Assignment>()
            emit(Resource.Success(assignments))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    override suspend fun createAssignment(assignment: Assignment): Resource<Unit> {
        return try {
            postgrest["assignments"].insert(assignment)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to create assignment")
        }
    }

    override fun getAssignmentsForClass(classId: String): Flow<Resource<List<Assignment>>> = flow {
        emit(Resource.Loading())
        try {
            val assignments = postgrest["assignments"]
                .select {
                    filter {
                        eq("classId", classId)
                    }
                }
                .decodeList<Assignment>()
            emit(Resource.Success(assignments))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }
}
