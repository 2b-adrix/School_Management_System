package com.example.schoolmanagementsystem.backend.repository

import com.example.schoolmanagementsystem.backend.model.Student
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StudentRepository : JpaRepository<Student, String> {
    fun findByUserEmail(email: String): Student?
    fun findBySchoolClassId(classId: String): List<Student>
}
