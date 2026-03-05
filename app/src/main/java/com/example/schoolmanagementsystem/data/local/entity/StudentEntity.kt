package com.example.schoolmanagementsystem.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.schoolmanagementsystem.domain.model.Student

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey val id: String,
    val firstName: String,
    val lastName: String,
    val rollNumber: String,
    val classId: String,
    val className: String,
    val parentName: String,
    val parentContact: String,
    val address: String,
    val dateOfBirth: String,
    val profileImageUrl: String? = null
)

fun StudentEntity.toDomain() = Student(
    id = id,
    firstName = firstName,
    lastName = lastName,
    rollNumber = rollNumber,
    classId = classId,
    className = className,
    parentName = parentName,
    parentContact = parentContact,
    address = address,
    dateOfBirth = dateOfBirth,
    profileImageUrl = profileImageUrl
)

fun Student.toEntity() = StudentEntity(
    id = id,
    firstName = firstName,
    lastName = lastName,
    rollNumber = rollNumber,
    classId = classId,
    className = className,
    parentName = parentName,
    parentContact = parentContact,
    address = address,
    dateOfBirth = dateOfBirth,
    profileImageUrl = profileImageUrl
)
