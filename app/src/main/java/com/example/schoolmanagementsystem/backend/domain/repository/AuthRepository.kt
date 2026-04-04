package com.example.schoolmanagementsystem.backend.domain.repository

import com.example.schoolmanagementsystem.backend.domain.model.User
import com.example.schoolmanagementsystem.backend.domain.model.UserRole
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): Resource<User>
    suspend fun logout()
    fun getCurrentUser(): Flow<User?>
    suspend fun signUp(email: String, password: String, role: UserRole, fullName: String, schoolId: String): Resource<User>
}

