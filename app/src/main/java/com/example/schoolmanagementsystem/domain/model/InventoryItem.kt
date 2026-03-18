package com.example.schoolmanagementsystem.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InventoryItem(
    @SerialName("id")
    val id: String,
    @SerialName("school_id")
    val schoolId: String,
    @SerialName("item_name")
    val itemName: String,
    @SerialName("category")
    val category: String, // e.g., Furniture, Stationery, Lab Equipment
    @SerialName("quantity")
    val quantity: Int,
    @SerialName("status")
    val status: String, // e.g., In Stock, Out of Stock, Damaged
    @SerialName("last_updated")
    val lastUpdated: String
)
