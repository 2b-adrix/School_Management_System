package com.example.schoolmanagementsystem.backend.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "salary_records")
data class SalaryRecord(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String? = null,
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,
    
    val amount: BigDecimal,
    val paymentDate: LocalDate,
    val month: String, // e.g. "October 2023"
    
    @Enumerated(EnumType.STRING)
    val status: PaymentStatus = PaymentStatus.PAID,
    
    val transactionId: String? = null
)
