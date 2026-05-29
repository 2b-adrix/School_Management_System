package com.example.schoolmanagementsystem.backend.repository

import com.example.schoolmanagementsystem.backend.model.ChatMessage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface MessageRepository : JpaRepository<ChatMessage, String> {
    
    @Query("SELECT m FROM ChatMessage m WHERE (m.sender.id = :user1 AND m.recipient.id = :user2) OR (m.sender.id = :user2 AND m.recipient.id = :user1) ORDER BY m.timestamp ASC")
    fun findConversation(user1: String, user2: String): List<ChatMessage>

    fun findByRecipientIdAndIsReadFalse(recipientId: String): List<ChatMessage>
}
