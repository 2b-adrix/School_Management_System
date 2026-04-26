package com.example.schoolmanagementsystem.backend.model

import jakarta.persistence.*

@Entity
@Table(name = "teachers")
data class Teacher(
    @Id
    val id: String, // Same as User ID
    
    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    val user: User,
    
    val specialization: String? = null,
    val salary: Double = 0.0,
    val joinedDate: String? = null
)
