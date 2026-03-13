package com.example.schoolmanagementsystem.domain.repository

import com.example.schoolmanagementsystem.domain.model.InventoryItem
import com.example.schoolmanagementsystem.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface InventoryRepository {
    fun getAllInventoryItems(): Flow<Resource<List<InventoryItem>>>
    suspend fun addInventoryItem(item: InventoryItem): Resource<Unit>
    suspend fun updateInventoryItem(item: InventoryItem): Resource<Unit>
    suspend fun deleteInventoryItem(itemId: String): Resource<Unit>
}
