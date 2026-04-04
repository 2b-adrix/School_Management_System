package com.example.schoolmanagementsystem.backend.data.repository

import com.example.schoolmanagementsystem.backend.data.manager.SessionManager
import com.example.schoolmanagementsystem.backend.domain.model.FeePayment
import com.example.schoolmanagementsystem.backend.domain.model.FeeStructure
import com.example.schoolmanagementsystem.backend.domain.repository.FeeRepository
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FeeRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val sessionManager: SessionManager
) : FeeRepository {

    override fun getFeeStructures(): Flow<Resource<List<FeeStructure>>> = flow {
        emit(Resource.Loading())
        try {
            val schoolId = sessionManager.schoolId.firstOrNull()
            val structures = postgrest["fee_structures"]
                .select {
                    filter {
                        eq("school_id", schoolId ?: "")
                    }
                }
                .decodeList<FeeStructure>()
            emit(Resource.Success(structures))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun addFeeStructure(feeStructure: FeeStructure): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            val structureWithSchoolId = feeStructure.copy(schoolId = schoolId)
            postgrest["fee_structures"].insert(structureWithSchoolId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add fee structure")
        }
    }

    override suspend fun updateFeeStructure(feeStructure: FeeStructure): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            postgrest["fee_structures"].update(feeStructure.copy(schoolId = schoolId)) {
                filter {
                    eq("id", feeStructure.id)
                    eq("school_id", schoolId)
                }
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update fee structure")
        }
    }

    override suspend fun deleteFeeStructure(feeStructure: FeeStructure): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            postgrest["fee_structures"].delete {
                filter {
                    eq("id", feeStructure.id)
                    eq("school_id", schoolId)
                }
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete fee structure")
        }
    }

    override fun getPaymentsByStudent(studentId: String): Flow<Resource<List<FeePayment>>> = flow {
        emit(Resource.Loading())
        try {
            val schoolId = sessionManager.schoolId.firstOrNull()
            val payments = postgrest["fee_payments"]
                .select {
                    filter {
                        eq("school_id", schoolId ?: "")
                        eq("student_id", studentId)
                    }
                }
                .decodeList<FeePayment>()
            emit(Resource.Success(payments))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun addPayment(payment: FeePayment): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            val paymentWithSchoolId = payment.copy(schoolId = schoolId)
            postgrest["fee_payments"].insert(paymentWithSchoolId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to record payment")
        }
    }

    override suspend fun deletePayment(payment: FeePayment): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            postgrest["fee_payments"].delete {
                filter {
                    eq("id", payment.id)
                    eq("school_id", schoolId)
                }
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete payment")
        }
    }
}

