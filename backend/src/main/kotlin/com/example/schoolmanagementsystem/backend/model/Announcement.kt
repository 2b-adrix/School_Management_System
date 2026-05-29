package com.example.schoolmanagementsystem.backend.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "announcements")
data class Announcement(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String? = null,
    
    @Column(nullable = false)
    val title: String,
    
    @Column(nullable = false, columnDefinition = "TEXT")
    val content: String,
    
    val targetRole: UserRole? = null, // null means everyone
    
    val schoolId: String,
    
    @ManyToOne
    @JoinColumn(name = "class_id")
    val schoolClass: SchoolClass? = null,
    
    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @ManyToOne
    @JoinColumn(name = "author_id")
    val author: User? = null
)
