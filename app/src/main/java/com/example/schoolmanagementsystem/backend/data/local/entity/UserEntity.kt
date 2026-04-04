package com.example.schoolmanagementsystem.backend.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.schoolmanagementsystem.backend.domain.model.User
import com.example.schoolmanagementsystem.backend.domain.model.UserRole

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val role: String,
    val schoolId: String,
    val lastSync: Long = System.currentTimeMillis()
) {
    fun toUser() = User(
        id = id,
        name = name,
        email = email,
        role = try { UserRole.valueOf(role) } catch (e: Exception) { UserRole.STUDENT },
        schoolId = schoolId
    )
}

fun User.toEntity() = UserEntity(
    id = id,
    name = name,
    email = email,
    role = role.name,
    schoolId = schoolId
)

