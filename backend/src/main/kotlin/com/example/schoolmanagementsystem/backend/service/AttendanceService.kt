package com.example.schoolmanagementsystem.backend.service

import com.example.schoolmanagementsystem.backend.model.AttendanceRecord
import com.example.schoolmanagementsystem.backend.repository.AttendanceRepository
import com.example.schoolmanagementsystem.backend.repository.StudentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class AttendanceService(
    private val attendanceRepository: AttendanceRepository,
    private val studentRepository: StudentRepository,
    private val geminiService: GeminiService
) {

    @Transactional
    fun markAttendance(record: AttendanceRecord): AttendanceRecord {
        val savedRecord = attendanceRepository.save(record)
        updateStudentAttendancePercentage(record.student.id)
        return savedRecord
    }

    fun getStudentAttendance(studentId: String): List<AttendanceRecord> {
        return attendanceRepository.findByStudentId(studentId)
    }

    fun getAttendanceByClassAndDate(classId: String, date: LocalDate): List<AttendanceRecord> {
        return attendanceRepository.findByStudentSchoolClassIdAndDate(classId, date)
    }

    private fun updateStudentAttendancePercentage(studentId: String) {
        val student = studentRepository.findById(studentId).orElseThrow()
        val records = attendanceRepository.findByStudentId(studentId)
        val presentCount = records.count { it.status.name == "PRESENT" || it.status.name == "LATE" }
        val percentage = (presentCount.toFloat() / records.size) * 100
        
        val updatedStudent = student.copy(attendancePercentage = percentage)
        studentRepository.save(updatedStudent)
    }
    
    fun getAIPerformanceInsight(studentId: String): String {
        val student = studentRepository.findById(studentId).orElseThrow()
        return geminiService.getAttendanceInsight(student.attendancePercentage)
    }
}
