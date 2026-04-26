package com.example.schoolmanagementsystem.backend.model

import jakarta.persistence.*

@Entity
@Table(name = "subjects")
data class Subject(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String? = null,
    
    @Column(nullable = false)
    val name: String,
    
    @ManyToOne
    @JoinColumn(name = "class_id")
    val schoolClass: SchoolClass,
    
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    val teacher: Teacher? = null
)
