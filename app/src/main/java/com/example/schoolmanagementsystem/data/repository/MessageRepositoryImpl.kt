package com.example.schoolmanagementsystem.data.repository

import com.example.schoolmanagementsystem.data.manager.SessionManager
import com.example.schoolmanagementsystem.domain.model.ChatMessage
import com.example.schoolmanagementsystem.domain.repository.MessageRepository
import com.example.schoolmanagementsystem.domain.util.Resource
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val sessionManager: SessionManager
) : MessageRepository {

    override fun getMessagesForUser(userId: String): Flow<Resource<List<ChatMessage>>> = flow {
        emit(Resource.Loading())
        try {
            val schoolId = sessionManager.schoolId.firstOrNull()
            val messages = postgrest["messages"]
                .select {
                    filter {
                        eq("school_id", schoolId ?: "")
                        or {
                            eq("sender_id", userId)
                            eq("receiver_id", userId)
                        }
                    }
                }
                .decodeList<ChatMessage>()
            emit(Resource.Success(messages))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    override fun getChatMessages(senderId: String, receiverId: String): Flow<Resource<List<ChatMessage>>> = flow {
        emit(Resource.Loading())
        try {
            val schoolId = sessionManager.schoolId.firstOrNull()
            val messages = postgrest["messages"]
                .select {
                    filter {
                        eq("school_id", schoolId ?: "")
                        or {
                            and {
                                eq("sender_id", senderId)
                                eq("receiver_id", receiverId)
                            }
                            and {
                                eq("sender_id", receiverId)
                                eq("receiver_id", senderId)
                            }
                        }
                    }
                }
                .decodeList<ChatMessage>()
            emit(Resource.Success(messages))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    override suspend fun sendMessage(message: ChatMessage): Resource<Unit> {
        return try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            postgrest["messages"].insert(message.copy(schoolId = schoolId))
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to send message")
        }
    }

    override suspend fun markAsRead(messageId: String): Resource<Unit> {
        return try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            postgrest["messages"].update({
                set("is_read", true)
            }) {
                filter {
                    eq("id", messageId)
                    eq("school_id", schoolId)
                }
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to mark as read")
        }
    }
}
