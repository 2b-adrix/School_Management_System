package com.example.schoolmanagementsystem.backend.controller

import com.example.schoolmanagementsystem.backend.model.Teacher
import com.example.schoolmanagementsystem.backend.service.TeacherService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/teachers")
class TeacherController(private val teacherService: TeacherService) {

    @GetMapping
    fun getAllTeachers(): List<Teacher> = teacherService.getAllTeachers()

    @GetMapping("/{id}")
    fun getTeacherById(@PathVariable id: String): ResponseEntity<Teacher> {
        return ResponseEntity.ok(teacherService.getTeacherById(id))
    }

    @PostMapping
    fun createTeacher(@RequestBody teacher: Teacher): ResponseEntity<Teacher> {
        return ResponseEntity.status(HttpStatus.CREATED).body(teacherService.createTeacher(teacher))
    }

    @PutMapping("/{id}")
    fun updateTeacher(@PathVariable id: String, @RequestBody teacher: Teacher): ResponseEntity<Teacher> {
        return ResponseEntity.ok(teacherService.updateTeacher(id, teacher))
    }

    @DeleteMapping("/{id}")
    fun deleteTeacher(@PathVariable id: String): ResponseEntity<Void> {
        teacherService.deleteTeacher(id)
        return ResponseEntity.noContent().build()
    }
}
