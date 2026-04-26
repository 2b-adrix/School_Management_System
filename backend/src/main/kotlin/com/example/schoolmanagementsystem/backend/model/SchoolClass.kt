package com.example.schoolmanagementsystem.backend.model

import jakarta.persistence.*

@Entity
@Table(name = "classes")
data class SchoolClass(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String? = null,
    
    @Column(nullable = false)
    val name: String, // e.g. "Grade 10-A"
    
    val schoolId: String,
    
    @OneToMany(mappedBy = "schoolClass")
    val students: List<Student> = emptyList()
)
