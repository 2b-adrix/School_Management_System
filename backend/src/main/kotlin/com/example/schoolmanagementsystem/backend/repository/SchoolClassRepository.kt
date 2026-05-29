package com.example.schoolmanagementsystem.backend.repository

import com.example.schoolmanagementsystem.backend.model.SchoolClass
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SchoolClassRepository : JpaRepository<SchoolClass, String> {
    fun findBySchoolId(schoolId: String): List<SchoolClass>
}
