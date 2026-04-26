package com.example.schoolmanagementsystem.backend.controller

import com.example.schoolmanagementsystem.backend.model.Student
import com.example.schoolmanagementsystem.backend.service.StudentService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/students")
class StudentController(private val studentService: StudentService) {

    @GetMapping("/profile")
    fun getProfile(): ResponseEntity<Student> {
        return ResponseEntity.ok(studentService.getCurrentStudentProfile())
    }
}
