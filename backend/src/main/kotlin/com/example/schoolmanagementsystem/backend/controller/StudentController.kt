package com.example.schoolmanagementsystem.backend.controller

import com.example.schoolmanagementsystem.backend.exception.ApiResponse
import com.example.schoolmanagementsystem.backend.model.Student
import com.example.schoolmanagementsystem.backend.service.StudentService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/students")
class StudentController(private val studentService: StudentService) {

    @GetMapping("/profile")
    fun getProfile(): ResponseEntity<ApiResponse<Student>> {
        val profile = studentService.getCurrentStudentProfile()
        return ResponseEntity.ok(ApiResponse.success(profile))
    }

    @GetMapping
    fun getAllStudents(): ResponseEntity<ApiResponse<List<Student>>> {
        return ResponseEntity.ok(ApiResponse.success(studentService.getAllStudents()))
    }

    @GetMapping("/{id}")
    fun getStudentById(@PathVariable id: String): ResponseEntity<ApiResponse<Student>> {
        return ResponseEntity.ok(ApiResponse.success(studentService.getStudentById(id)))
    }

    @PostMapping
    fun addStudent(@RequestBody student: Student): ResponseEntity<ApiResponse<Student>> {
        return ResponseEntity.ok(ApiResponse.success(studentService.saveStudent(student), "Student added successfully"))
    }

    @PutMapping("/{id}")
    fun updateStudent(@PathVariable id: String, @RequestBody student: Student): ResponseEntity<ApiResponse<Student>> {
        return ResponseEntity.ok(ApiResponse.success(studentService.updateStudent(id, student), "Student updated successfully"))
    }

    @DeleteMapping("/{id}")
    fun deleteStudent(@PathVariable id: String): ResponseEntity<ApiResponse<Unit>> {
        studentService.deleteStudent(id)
        return ResponseEntity.ok(ApiResponse.success(Unit, "Student deleted successfully"))
    }
}
