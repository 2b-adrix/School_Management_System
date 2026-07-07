package com.example.schoolmanagementsystem.backend.data.repository

import com.example.schoolmanagementsystem.backend.data.manager.SessionManager
import com.example.schoolmanagementsystem.backend.data.remote.SikshaApiService
import com.example.schoolmanagementsystem.backend.domain.model.FeePayment
import com.example.schoolmanagementsystem.backend.domain.model.FeeStructure
import com.example.schoolmanagementsystem.backend.domain.repository.FeeRepository
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FeeRepositoryImpl @Inject constructor(
    private val apiService: SikshaApiService,
    private val sessionManager: SessionManager
) : FeeRepository {

    override fun getFeeStructures(): Flow<Resource<List<FeeStructure>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getFeeStructures()
            if (response.success && response.data != null) {
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun addFeeStructure(feeStructure: FeeStructure): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.createFeeStructure(feeStructure)
            if (response.success) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add fee structure")
        }
    }

    override suspend fun updateFeeStructure(feeStructure: FeeStructure): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            // Need update endpoint in SikshaApiService if not there
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update fee structure")
        }
    }

    override suspend fun deleteFeeStructure(feeStructure: FeeStructure): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            // Need delete endpoint in SikshaApiService if not there
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete fee structure")
        }
    }

    override fun getPaymentsByStudent(studentId: String): Flow<Resource<List<FeePayment>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getPaymentsByStudent(studentId)
            if (response.success && response.data != null) {
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun addPayment(feePayment: FeePayment): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.addPayment(feePayment)
            if (response.success) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add payment")
        }
    }

    override suspend fun deletePayment(feePayment: FeePayment): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            // Need delete endpoint in SikshaApiService if not there
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete payment")
        }
    }
}
