package com.example.schoolmanagementsystem.backend.data.repository

import com.example.schoolmanagementsystem.backend.data.manager.SessionManager
import com.example.schoolmanagementsystem.backend.data.remote.SikshaApiService
import com.example.schoolmanagementsystem.backend.domain.model.InventoryItem
import com.example.schoolmanagementsystem.backend.domain.repository.InventoryRepository
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject

class InventoryRepositoryImpl @Inject constructor(
    private val apiService: SikshaApiService,
    private val sessionManager: SessionManager
) : InventoryRepository {

    override fun getAllInventoryItems(): Flow<Resource<List<InventoryItem>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getAllInventoryItems()
            if (response.success && response.data != null) {
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun addInventoryItem(item: InventoryItem): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.addInventoryItem(item)
            if (response.success) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add inventory item")
        }
    }

    override suspend fun updateInventoryItem(item: InventoryItem): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.updateInventoryItem(item.id, item)
            if (response.success) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update inventory item")
        }
    }

    override suspend fun deleteInventoryItem(id: String): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.deleteInventoryItem(id)
            if (response.success) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete inventory item")
        }
    }
}
