package com.example.schoolmanagementsystem.backend.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "assignments")
data class Assignment(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String? = null,
    
    @Column(nullable = false)
    val title: String,
    
    val description: String? = null,
    
    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    val subject: Subject,
    
    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    val schoolClass: SchoolClass,
    
    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    val teacher: Teacher,
    
    val dueDate: LocalDateTime,
    val maxPoints: Int = 100,
    
    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)
