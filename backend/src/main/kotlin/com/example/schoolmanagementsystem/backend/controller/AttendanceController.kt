package com.example.schoolmanagementsystem.backend.controller

import com.example.schoolmanagementsystem.backend.exception.ApiResponse
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
    fun markAttendance(@RequestBody record: AttendanceRecord): ResponseEntity<ApiResponse<AttendanceRecord>> {
        val saved = attendanceService.markAttendance(record)
        return ResponseEntity.ok(ApiResponse.success(saved, "Attendance marked successfully"))
    }

    @GetMapping("/student/{studentId}")
    fun getStudentAttendance(@PathVariable studentId: String): ResponseEntity<ApiResponse<List<AttendanceRecord>>> {
        return ResponseEntity.ok(ApiResponse.success(attendanceService.getStudentAttendance(studentId)))
    }

    @GetMapping("/class/{classId}")
    fun getClassAttendance(
        @PathVariable classId: String,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate
    ): ResponseEntity<ApiResponse<List<AttendanceRecord>>> {
        return ResponseEntity.ok(ApiResponse.success(attendanceService.getAttendanceByClassAndDate(classId, date)))
    }
    
    @GetMapping("/student/{studentId}/insight")
    fun getAttendanceInsight(@PathVariable studentId: String): ResponseEntity<ApiResponse<Map<String, String>>> {
        val insight = attendanceService.getAIPerformanceInsight(studentId)
        return ResponseEntity.ok(ApiResponse.success(mapOf("insight" to insight)))
    }
}
