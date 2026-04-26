package com.example.schoolmanagementsystem.backend.model

import jakarta.persistence.*

@Entity
@Table(name = "students")
data class Student(
    @Id
    val id: String, // Same as User ID
    
    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    val user: User,
    
    @Column(unique = true)
    val rollNumber: String,
    
    @ManyToOne
    @JoinColumn(name = "class_id")
    val schoolClass: SchoolClass? = null,
    
    val parentContact: String? = null,
    val photoUrl: String? = null,
    val attendancePercentage: Float = 0f
)
