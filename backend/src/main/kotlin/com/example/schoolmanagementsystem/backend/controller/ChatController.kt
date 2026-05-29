package com.example.schoolmanagementsystem.backend.controller

import com.example.schoolmanagementsystem.backend.model.ChatMessage
import com.example.schoolmanagementsystem.backend.repository.MessageRepository
import com.example.schoolmanagementsystem.backend.repository.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/chat")
class ChatController(
    private val messageRepository: MessageRepository,
    private val userRepository: UserRepository
) {

    @GetMapping("/conversation/{recipientId}")
    fun getConversation(@PathVariable recipientId: String): ResponseEntity<List<ChatMessage>> {
        val senderEmail = SecurityContextHolder.getContext().authentication.name
        val sender = userRepository.findByEmail(senderEmail) ?: return ResponseEntity.notFound().build()
        
        return ResponseEntity.ok(messageRepository.findConversation(sender.id!!, recipientId))
    }

    @PostMapping("/send")
    fun sendMessage(@RequestBody request: SendMessageRequest): ResponseEntity<ChatMessage> {
        val senderEmail = SecurityContextHolder.getContext().authentication.name
        val sender = userRepository.findByEmail(senderEmail) ?: return ResponseEntity.notFound().build()
        val recipient = userRepository.findById(request.recipientId).orElse(null) ?: return ResponseEntity.badRequest().build()

        val message = ChatMessage(
            sender = sender,
            recipient = recipient,
            content = request.content
        )
        
        return ResponseEntity.ok(messageRepository.save(message))
    }
}

data class SendMessageRequest(val recipientId: String, val content: String)
