package com.example.schoolmanagementsystem.backend.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "exams")
data class Exam(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String? = null,
    
    @Column(nullable = false)
    val title: String,
    
    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    val subject: Subject,
    
    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    val schoolClass: SchoolClass,
    
    val startTime: LocalDateTime,
    val durationMinutes: Int,
    
    val totalMarks: Int = 100,
    val passingMarks: Int = 33,
    
    @OneToMany(mappedBy = "exam", cascade = [CascadeType.ALL])
    val results: List<ExamResult> = emptyList()
)

@Entity
@Table(name = "exam_results")
data class ExamResult(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String? = null,
    
    @ManyToOne
    @JoinColumn(name = "exam_id", nullable = false)
    val exam: Exam,
    
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    val student: Student,
    
    val obtainedMarks: Float,
    val remarks: String? = null
)
