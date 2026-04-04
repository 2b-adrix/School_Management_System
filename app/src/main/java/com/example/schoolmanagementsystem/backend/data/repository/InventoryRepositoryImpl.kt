package com.example.schoolmanagementsystem.backend.data.repository

import com.example.schoolmanagementsystem.backend.data.manager.SessionManager
import com.example.schoolmanagementsystem.backend.domain.model.InventoryItem
import com.example.schoolmanagementsystem.backend.domain.repository.InventoryRepository
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class InventoryRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val sessionManager: SessionManager
) : InventoryRepository {

    override fun getAllInventoryItems(): Flow<Resource<List<InventoryItem>>> = flow {
        emit(Resource.Loading())
        try {
            val schoolId = sessionManager.schoolId.firstOrNull()
            val items = postgrest["inventory"]
                .select {
                    filter {
                        eq("school_id", schoolId ?: "")
                    }
                }
                .decodeList<InventoryItem>()
            emit(Resource.Success(items))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    override suspend fun addInventoryItem(item: InventoryItem): Resource<Unit> {
        return try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            postgrest["inventory"].insert(item.copy(schoolId = schoolId))
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add item")
        }
    }

    override suspend fun updateInventoryItem(item: InventoryItem): Resource<Unit> {
        return try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            postgrest["inventory"].update(item.copy(schoolId = schoolId)) {
                filter {
                    eq("id", item.id)
                    eq("school_id", schoolId)
                }
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update item")
        }
    }

    override suspend fun deleteInventoryItem(itemId: String): Resource<Unit> {
        return try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            postgrest["inventory"].delete {
                filter {
                    eq("id", itemId)
                    eq("school_id", schoolId)
                }
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete item")
        }
    }
}

