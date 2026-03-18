package com.example.schoolmanagementsystem.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FeePayment(
    @SerialName("id")
    val id: String,
    @SerialName("school_id")
    val schoolId: String,
    @SerialName("student_id")
    val studentId: String,
    @SerialName("fee_structure_id")
    val feeStructureId: String,
    @SerialName("amount_paid")
    val amountPaid: Double,
    @SerialName("payment_date")
    val paymentDate: String,
    @SerialName("payment_method")
    val paymentMethod: String,
    @SerialName("status")
    val status: String // e.g., PAID, PARTIAL, PENDING
)
