package com.example.schoolmanagementsystem.backend.controller

import com.example.schoolmanagementsystem.backend.model.AttendanceRecord
import com.example.schoolmanagementsystem.backend.service.AttendanceService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/v1/attendance")
class AttendanceController(private val attendanceService: AttendanceService) {

    @PostMapping
    fun markAttendance(@RequestBody record: AttendanceRecord): ResponseEntity<AttendanceRecord> {
        return ResponseEntity.ok(attendanceService.markAttendance(record))
    }

    @GetMapping("/student/{studentId}")
    fun getStudentAttendance(@PathVariable studentId: String): List<AttendanceRecord> {
        return attendanceService.getStudentAttendance(studentId)
    }

    @GetMapping("/class/{classId}")
    fun getClassAttendance(
        @PathVariable classId: String,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate
    ): List<AttendanceRecord> {
        return attendanceService.getAttendanceByClassAndDate(classId, date)
    }
    
    @GetMapping("/student/{studentId}/insight")
    fun getAttendanceInsight(@PathVariable studentId: String): ResponseEntity<Map<String, String>> {
        val insight = attendanceService.getAIPerformanceInsight(studentId)
        return ResponseEntity.ok(mapOf("insight" to insight))
    }
}
