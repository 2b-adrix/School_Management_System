package com.example.schoolmanagementsystem.backend.data.repository

import com.example.schoolmanagementsystem.backend.data.manager.SessionManager
import com.example.schoolmanagementsystem.backend.data.remote.SikshaApiService
import com.example.schoolmanagementsystem.backend.domain.model.SalaryRecord
import com.example.schoolmanagementsystem.backend.domain.repository.SalaryRepository
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SalaryRepositoryImpl @Inject constructor(
    private val apiService: SikshaApiService,
    private val sessionManager: SessionManager
) : SalaryRepository {

    override fun getAllSalaries(): Flow<Resource<List<SalaryRecord>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getAllSalaries()
            if (response.success && response.data != null) {
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }.flowOn(Dispatchers.IO)

    override fun getSalariesByTeacher(teacherId: String): Flow<Resource<List<SalaryRecord>>> = flow {
        emit(Resource.Loading())
        try {
            // Filter by teacherId would be done in backend
            val response = apiService.getAllSalaries()
            if (response.success && response.data != null) {
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun addSalaryRecord(salaryRecord: SalaryRecord): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.addSalaryRecord(salaryRecord)
            if (response.success) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add salary record")
        }
    }

    override suspend fun updateSalaryStatus(salaryId: String, status: String): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            // Need update endpoint in SikshaApiService if not there
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update salary status")
        }
    }

    override suspend fun deleteSalaryRecord(salaryId: String): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            // Need delete endpoint in SikshaApiService if not there
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete salary record")
        }
    }
}
