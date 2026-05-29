package com.example.schoolmanagementsystem.backend.repository

import com.example.schoolmanagementsystem.backend.model.AttendanceRecord
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface AttendanceRepository : JpaRepository<AttendanceRecord, String> {
    fun findByStudentId(studentId: String): List<AttendanceRecord>
    fun findByStudentSchoolClassIdAndDate(classId: String, date: LocalDate): List<AttendanceRecord>
}
