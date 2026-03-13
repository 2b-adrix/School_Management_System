package com.example.schoolmanagementsystem.domain.repository

import com.example.schoolmanagementsystem.domain.model.ChatMessage
import com.example.schoolmanagementsystem.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    fun getMessagesForUser(userId: String): Flow<Resource<List<ChatMessage>>>
    fun getChatMessages(senderId: String, receiverId: String): Flow<Resource<List<ChatMessage>>>
    suspend fun sendMessage(message: ChatMessage): Resource<Unit>
    suspend fun markAsRead(messageId: String): Resource<Unit>
}
