package com.example.schoolmanagementsystem.backend.controller

import com.example.schoolmanagementsystem.backend.model.Subject
import com.example.schoolmanagementsystem.backend.repository.SubjectRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/subjects")
class SubjectController(private val subjectRepository: SubjectRepository) {

    @GetMapping
    fun getAllSubjects(): List<Subject> = subjectRepository.findAll()

    @GetMapping("/school/{schoolId}")
    fun getSubjectsBySchool(@PathVariable schoolId: String): List<Subject> = 
        subjectRepository.findBySchoolId(schoolId)

    @PostMapping
    fun createSubject(@RequestBody subject: Subject): Subject = subjectRepository.save(subject)
}
