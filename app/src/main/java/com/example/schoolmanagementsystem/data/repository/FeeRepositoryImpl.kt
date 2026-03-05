package com.example.schoolmanagementsystem.data.repository

import com.example.schoolmanagementsystem.domain.model.FeePayment
import com.example.schoolmanagementsystem.domain.model.FeeStructure
import com.example.schoolmanagementsystem.domain.repository.FeeRepository
import com.example.schoolmanagementsystem.domain.util.Resource
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FeeRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest
) : FeeRepository {

    override fun getFeeStructures(): Flow<Resource<List<FeeStructure>>> = flow {
        emit(Resource.Loading())
        try {
            val structures = postgrest["fee_structures"]
                .select()
                .decodeList<FeeStructure>()
            emit(Resource.Success(structures))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    override suspend fun addFeeStructure(feeStructure: FeeStructure): Resource<Unit> {
        return try {
            postgrest["fee_structures"].insert(feeStructure)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add fee structure")
        }
    }

    override fun getPaymentsByStudent(studentId: String): Flow<Resource<List<FeePayment>>> = flow {
        emit(Resource.Loading())
        try {
            val payments = postgrest["fee_payments"]
                .select {
                    filter {
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
            postgrest["fee_payments"].insert(payment)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to record payment")
        }
    }
}
