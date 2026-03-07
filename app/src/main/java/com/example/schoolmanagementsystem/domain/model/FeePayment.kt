package com.example.schoolmanagementsystem.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class FeePayment(
    val id: String,
    val schoolId: String,
    val studentId: String,
    val feeStructureId: String,
    val amountPaid: Double,
    val paymentDate: String,
    val paymentMethod: String,
    val status: String // e.g., PAID, PARTIAL, PENDING
)
