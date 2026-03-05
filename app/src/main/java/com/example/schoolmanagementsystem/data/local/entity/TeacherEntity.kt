package com.example.schoolmanagementsystem.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.schoolmanagementsystem.domain.model.Teacher

@Entity(tableName = "teachers")
data class TeacherEntity(
    @PrimaryKey val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val subjects: String, // Comma separated for simplicity in this example
    val assignedClasses: String,
    val profileImageUrl: String? = null
)

fun TeacherEntity.toDomain() = Teacher(
    id = id,
    firstName = firstName,
    lastName = lastName,
    email = email,
    phoneNumber = phoneNumber,
    subjects = subjects.split(","),
    assignedClasses = assignedClasses.split(","),
    profileImageUrl = profileImageUrl
)

fun Teacher.toEntity() = TeacherEntity(
    id = id,
    firstName = firstName,
    lastName = lastName,
    email = email,
    phoneNumber = phoneNumber,
    subjects = subjects.joinToString(","),
    assignedClasses = assignedClasses.joinToString(","),
    profileImageUrl = profileImageUrl
)
