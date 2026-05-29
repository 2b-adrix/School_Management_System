package com.example.schoolmanagementsystem.backend.service

import com.example.schoolmanagementsystem.backend.exception.ResourceNotFoundException
import com.example.schoolmanagementsystem.backend.model.Teacher
import com.example.schoolmanagementsystem.backend.repository.TeacherRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TeacherService(private val teacherRepository: TeacherRepository) {

    fun getAllTeachers(): List<Teacher> = teacherRepository.findAll()

    fun getTeacherById(id: String): Teacher {
        return teacherRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Teacher not found with id: $id") }
    }

    @Transactional
    fun createTeacher(teacher: Teacher): Teacher {
        return teacherRepository.save(teacher)
    }

    @Transactional
    fun updateTeacher(id: String, teacherDetails: Teacher): Teacher {
        val teacher = getTeacherById(id)
        val updatedTeacher = teacher.copy(
            phoneNumber = teacherDetails.phoneNumber,
            qualification = teacherDetails.qualification,
            profileImageUrl = teacherDetails.profileImageUrl
        )
        return teacherRepository.save(updatedTeacher)
    }

    @Transactional
    fun deleteTeacher(id: String) {
        val teacher = getTeacherById(id)
        teacherRepository.delete(teacher)
    }
}
