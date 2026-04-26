package com.example.schoolmanagementsystem.backend.model

import jakarta.persistence.*

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String? = null,
    
    @Column(nullable = false)
    val name: String,
    
    @Column(unique = true, nullable = false)
    val email: String,
    
    @Column(nullable = false)
    val passwordHash: String,
    
    @Enumerated(EnumType.STRING)
    val role: UserRole,
    
    val schoolId: String
)

enum class UserRole {
    STUDENT, TEACHER, SCHOOL_ADMIN, SUPER_ADMIN
}
