package com.example.schoolmanagementsystem.backend.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "inventory_items")
data class InventoryItem(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String? = null,
    
    val name: String,
    val category: String,
    val quantity: Int,
    val schoolId: String,
    
    val location: String? = null,
    val lastUpdated: LocalDateTime = LocalDateTime.now()
)
