package com.example.schoolmanagementsystem.backend.data.repository

import com.example.schoolmanagementsystem.backend.data.manager.SessionManager
import com.example.schoolmanagementsystem.backend.data.remote.SikshaApiService
import com.example.schoolmanagementsystem.backend.domain.model.Assignment
import com.example.schoolmanagementsystem.backend.domain.repository.AssignmentRepository
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AssignmentRepositoryImpl @Inject constructor(
    private val apiService: SikshaApiService,
    private val sessionManager: SessionManager
) : AssignmentRepository {

    override fun getAllAssignments(): Flow<Resource<List<Assignment>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getAllAssignments()
            if (response.success && response.data != null) {
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun createAssignment(assignment: Assignment): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.createAssignment(assignment)
            if (response.success) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to create assignment")
        }
    }

    override suspend fun deleteAssignment(id: String): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.deleteAssignment(id)
            if (response.success) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete assignment")
        }
    }

    override fun getAssignmentsForClass(classId: String): Flow<Resource<List<Assignment>>> = flow {
        emit(Resource.Loading())
        try {
            // For now, filtering logic would be in backend
            val response = apiService.getAllAssignments()
            if (response.success && response.data != null) {
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }.flowOn(Dispatchers.IO)
}
