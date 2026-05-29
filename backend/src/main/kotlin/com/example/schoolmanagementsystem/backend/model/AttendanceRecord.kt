package com.example.schoolmanagementsystem.backend.model

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "attendance")
data class AttendanceRecord(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String? = null,
    
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    val student: Student,
    
    @Column(nullable = false)
    val date: LocalDate = LocalDate.now(),
    
    @Enumerated(EnumType.STRING)
    val status: AttendanceStatus,
    
    val remarks: String? = null
)

enum class AttendanceStatus {
    PRESENT, ABSENT, LATE, EXCUSED
}
