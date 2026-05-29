package com.example.schoolmanagementsystem.backend.repository

import com.example.schoolmanagementsystem.backend.model.Teacher
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TeacherRepository : JpaRepository<Teacher, String> {
    fun findByUserEmail(email: String): Teacher?
}
