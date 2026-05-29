package com.example.schoolmanagementsystem.backend.model

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "teachers")
data class Teacher(
    @Id
    val id: String, // Same as User ID
    
    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    val user: User,
    
    @Column(nullable = false)
    val phoneNumber: String,
    
    val qualification: String,
    
    val joinDate: LocalDate = LocalDate.now(),
    
    val profileImageUrl: String? = null,
    
    @ManyToMany
    @JoinTable(
        name = "teacher_subjects",
        joinColumns = [JoinColumn(name = "teacher_id")],
        inverseJoinColumns = [JoinColumn(name = "subject_id")]
    )
    val subjects: List<Subject> = emptyList(),

    @ManyToMany
    @JoinTable(
        name = "teacher_classes",
        joinColumns = [JoinColumn(name = "teacher_id")],
        inverseJoinColumns = [JoinColumn(name = "class_id")]
    )
    val assignedClasses: List<SchoolClass> = emptyList()
)
