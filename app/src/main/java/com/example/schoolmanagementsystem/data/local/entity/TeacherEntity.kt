package com.example.schoolmanagementsystem.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.schoolmanagementsystem.domain.model.Teacher

@Entity(tableName = "teachers")
data class TeacherEntity(
    @PrimaryKey val id: String,
    val schoolId: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val qualification: String,
    val joinDate: String,
    val subjects: String, // Comma separated
    val assignedClasses: String, // Comma separated
    val profileImageUrl: String? = null
)

fun TeacherEntity.toDomain() = Teacher(
    id = id,
    schoolId = schoolId,
    firstName = firstName,
    lastName = lastName,
    email = email,
    phoneNumber = phoneNumber,
    qualification = qualification,
    joinDate = joinDate,
    subjects = if (subjects.isEmpty()) emptyList() else subjects.split(","),
    assignedClasses = if (assignedClasses.isEmpty()) emptyList() else assignedClasses.split(","),
    profileImageUrl = profileImageUrl
)

fun Teacher.toEntity() = TeacherEntity(
    id = id,
    schoolId = schoolId,
    firstName = firstName,
    lastName = lastName,
    email = email,
    phoneNumber = phoneNumber,
    qualification = qualification,
    joinDate = joinDate,
    subjects = subjects.joinToString(","),
    assignedClasses = assignedClasses.joinToString(","),
    profileImageUrl = profileImageUrl
)
