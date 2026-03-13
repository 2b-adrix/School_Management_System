package com.example.schoolmanagementsystem.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SalaryRecord(
    val id: String,
    val schoolId: String,
    val teacherId: String,
    val teacherName: String,
    val amount: Double,
    val month: String, // e.g., "October 2025"
    val paymentDate: String,
    val status: String, // PAID, PENDING
    val transactionId: String? = null
)
