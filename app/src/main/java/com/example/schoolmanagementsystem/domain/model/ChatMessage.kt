package com.example.schoolmanagementsystem.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(
    val id: String,
    val schoolId: String,
    val senderId: String,
    val senderName: String,
    val senderRole: String,
    val receiverId: String, // Can be a classId or a specific userId
    val content: String,
    val createdAt: String,
    val isRead: Boolean = false
)
