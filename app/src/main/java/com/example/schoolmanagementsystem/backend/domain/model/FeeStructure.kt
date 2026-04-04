package com.example.schoolmanagementsystem.backend.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FeeStructure(
    @SerialName("id")
    val id: String,
    @SerialName("school_id")
    val schoolId: String,
    @SerialName("class_id")
    val classId: String,
    @SerialName("fee_name")
    val feeName: String,
    @SerialName("amount")
    val amount: Double,
    @SerialName("due_date")
    val dueDate: String? = null,
    @SerialName("description")
    val description: String? = null
)

