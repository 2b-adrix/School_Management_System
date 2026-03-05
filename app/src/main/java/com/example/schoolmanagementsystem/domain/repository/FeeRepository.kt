package com.example.schoolmanagementsystem.domain.repository

import com.example.schoolmanagementsystem.domain.model.FeePayment
import com.example.schoolmanagementsystem.domain.model.FeeStructure
import com.example.schoolmanagementsystem.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface FeeRepository {
    fun getFeeStructures(): Flow<Resource<List<FeeStructure>>>
    suspend fun addFeeStructure(feeStructure: FeeStructure): Resource<Unit>
    fun getPaymentsByStudent(studentId: String): Flow<Resource<List<FeePayment>>>
    suspend fun addPayment(payment: FeePayment): Resource<Unit>
}
