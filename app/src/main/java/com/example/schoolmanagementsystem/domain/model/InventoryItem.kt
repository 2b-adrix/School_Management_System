package com.example.schoolmanagementsystem.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class InventoryItem(
    val id: String,
    val schoolId: String,
    val itemName: String,
    val category: String, // e.g., Furniture, Stationery, Lab Equipment
    val quantity: Int,
    val status: String, // e.g., In Stock, Out of Stock, Damaged
    val lastUpdated: String
)
