package com.example.schoolmanagementsystem.backend.data.repository

import com.example.schoolmanagementsystem.backend.data.manager.SessionManager
import com.example.schoolmanagementsystem.backend.data.remote.SikshaApiService
import com.example.schoolmanagementsystem.backend.domain.model.ChatMessage
import com.example.schoolmanagementsystem.backend.domain.repository.MessageRepository
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor(
    private val apiService: SikshaApiService,
    private val sessionManager: SessionManager
) : MessageRepository {

    override fun getMessagesForUser(userId: String): Flow<Resource<List<ChatMessage>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getMessagesForUser()
            if (response.success && response.data != null) {
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }.flowOn(Dispatchers.IO)

    override fun getChatMessages(userId: String, otherUserId: String): Flow<Resource<List<ChatMessage>>> = flow {
        emit(Resource.Loading())
        try {
            // For now, use the same as getMessagesForUser or filter in backend
            val response = apiService.getMessagesForUser()
            if (response.success && response.data != null) {
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun sendMessage(message: ChatMessage): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.sendMessage(message)
            if (response.success) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to send message")
        }
    }

    override suspend fun markAsRead(messageId: String): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            // Need markAsRead endpoint in SikshaApiService if not there
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to mark as read")
        }
    }
}
