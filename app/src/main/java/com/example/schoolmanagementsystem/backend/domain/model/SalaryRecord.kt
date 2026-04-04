package com.example.schoolmanagementsystem.backend.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SalaryRecord(
    @SerialName("id")
    val id: String,
    @SerialName("school_id")
    val schoolId: String,
    @SerialName("teacher_id")
    val teacherId: String,
    @SerialName("teacher_name")
    val teacherName: String,
    @SerialName("amount")
    val amount: Double,
    @SerialName("month")
    val month: String, // e.g., "October 2025"
    @SerialName("payment_date")
    val paymentDate: String,
    @SerialName("status")
    val status: String, // PAID, PENDING
    @SerialName("transaction_id")
    val transactionId: String? = null
)

