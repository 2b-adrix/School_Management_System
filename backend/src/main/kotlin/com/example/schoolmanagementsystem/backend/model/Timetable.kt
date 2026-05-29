package com.example.schoolmanagementsystem.backend.model

import jakarta.persistence.*
import java.time.LocalTime

@Entity
@Table(name = "timetable")
data class TimetableEntry(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String? = null,
    
    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    val schoolClass: SchoolClass,
    
    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    val subject: Subject,
    
    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    val teacher: Teacher,
    
    @Enumerated(EnumType.STRING)
    val dayOfWeek: DayOfWeek,
    
    val startTime: LocalTime,
    val endTime: LocalTime,
    
    val roomNumber: String? = null
)

enum class DayOfWeek {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
}
