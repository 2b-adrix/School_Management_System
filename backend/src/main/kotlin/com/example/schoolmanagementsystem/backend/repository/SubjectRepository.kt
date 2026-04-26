package com.example.schoolmanagementsystem.backend.repository

import com.example.schoolmanagementsystem.backend.model.Subject
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SubjectRepository : JpaRepository<Subject, String> {
    fun findBySchoolClassId(classId: String): List<Subject>
}
