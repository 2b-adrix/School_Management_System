package com.example.schoolmanagementsystem.backend.controller

import com.example.schoolmanagementsystem.backend.exception.ApiResponse
import com.example.schoolmanagementsystem.backend.model.Teacher
import com.example.schoolmanagementsystem.backend.service.TeacherService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/teachers")
class TeacherController(private val teacherService: TeacherService) {

    @GetMapping
    fun getAllTeachers(): ResponseEntity<ApiResponse<List<Teacher>>> {
        return ResponseEntity.ok(ApiResponse.success(teacherService.getAllTeachers()))
    }

    @GetMapping("/{id}")
    fun getTeacherById(@PathVariable id: String): ResponseEntity<ApiResponse<Teacher>> {
        return ResponseEntity.ok(ApiResponse.success(teacherService.getTeacherById(id)))
    }

    @PostMapping
    fun createTeacher(@RequestBody teacher: Teacher): ResponseEntity<ApiResponse<Teacher>> {
        val created = teacherService.createTeacher(teacher)
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created, "Teacher created successfully"))
    }

    @PutMapping("/{id}")
    fun updateTeacher(@PathVariable id: String, @RequestBody teacher: Teacher): ResponseEntity<ApiResponse<Teacher>> {
        val updated = teacherService.updateTeacher(id, teacher)
        return ResponseEntity.ok(ApiResponse.success(updated, "Teacher updated successfully"))
    }

    @DeleteMapping("/{id}")
    fun deleteTeacher(@PathVariable id: String): ResponseEntity<ApiResponse<Unit>> {
        teacherService.deleteTeacher(id)
        return ResponseEntity.ok(ApiResponse.success(Unit, "Teacher deleted successfully"))
    }
}
