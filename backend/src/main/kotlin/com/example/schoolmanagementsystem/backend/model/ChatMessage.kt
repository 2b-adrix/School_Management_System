package com.example.schoolmanagementsystem.backend.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "chat_messages")
data class ChatMessage(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String? = null,
    
    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    val sender: User,
    
    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    val recipient: User,
    
    @Column(nullable = false, columnDefinition = "TEXT")
    val content: String,
    
    @Column(nullable = false)
    val timestamp: LocalDateTime = LocalDateTime.now(),
    
    val isRead: Boolean = false
)
