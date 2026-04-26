package com.example.schoolmanagementsystem.backend.repository

import com.example.schoolmanagementsystem.backend.model.Student
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StudentRepository : JpaRepository<Student, String> {
    fun findByRollNumber(rollNumber: String): Student?
}
