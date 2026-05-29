package com.example.schoolmanagementsystem.backend.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "fee_structures")
data class FeeStructure(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String? = null,
    
    val name: String, // e.g. "Monthly Tuition"
    val amount: BigDecimal,
    val dueDate: LocalDateTime,
    val schoolId: String,
    
    @ManyToOne
    @JoinColumn(name = "class_id")
    val schoolClass: SchoolClass? = null
)

@Entity
@Table(name = "fee_payments")
data class FeePayment(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String? = null,
    
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    val student: Student,
    
    @ManyToOne
    @JoinColumn(name = "fee_structure_id", nullable = false)
    val feeStructure: FeeStructure,
    
    val amountPaid: BigDecimal,
    val paymentDate: LocalDateTime = LocalDateTime.now(),
    
    @Enumerated(EnumType.STRING)
    val status: PaymentStatus = PaymentStatus.PAID,
    
    val transactionId: String? = null
)

enum class PaymentStatus {
    PAID, PARTIAL, PENDING, OVERDUE
}
