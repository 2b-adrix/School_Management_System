package com.example.schoolmanagementsystem.data.repository

import com.example.schoolmanagementsystem.data.manager.SessionManager
import com.example.schoolmanagementsystem.domain.model.FeePayment
import com.example.schoolmanagementsystem.domain.model.FeeStructure
import com.example.schoolmanagementsystem.domain.repository.FeeRepository
import com.example.schoolmanagementsystem.domain.util.Resource
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
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
                        eq("schoolId", schoolId ?: "")
                    }
                }
                .decodeList<FeeStructure>()
            emit(Resource.Success(structures))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    override suspend fun addFeeStructure(feeStructure: FeeStructure): Resource<Unit> {
        return try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            val structureWithSchoolId = feeStructure.copy(schoolId = schoolId)
            postgrest["fee_structures"].insert(structureWithSchoolId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add fee structure")
        }
    }

    override fun getPaymentsByStudent(studentId: String): Flow<Resource<List<FeePayment>>> = flow {
        emit(Resource.Loading())
        try {
            val schoolId = sessionManager.schoolId.firstOrNull()
            val payments = postgrest["fee_payments"]
                .select {
                    filter {
                        eq("schoolId", schoolId ?: "")
                        eq("studentId", studentId)
                    }
                }
                .decodeList<FeePayment>()
            emit(Resource.Success(payments))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    override suspend fun addPayment(payment: FeePayment): Resource<Unit> {
        return try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            val paymentWithSchoolId = payment.copy(schoolId = schoolId)
            postgrest["fee_payments"].insert(paymentWithSchoolId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to record payment")
        }
    }
}
