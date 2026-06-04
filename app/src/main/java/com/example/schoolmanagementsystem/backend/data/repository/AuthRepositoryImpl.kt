package com.example.schoolmanagementsystem.backend.data.repository

import com.example.schoolmanagementsystem.backend.data.local.dao.UserDao
import com.example.schoolmanagementsystem.backend.data.local.entity.toEntity
import com.example.schoolmanagementsystem.backend.data.manager.SessionManager
import com.example.schoolmanagementsystem.backend.domain.model.User
import com.example.schoolmanagementsystem.backend.domain.model.UserRole
import com.example.schoolmanagementsystem.backend.domain.repository.AuthRepository
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import com.example.schoolmanagementsystem.backend.data.remote.LoginRequest
import com.example.schoolmanagementsystem.backend.data.remote.SignUpRequest
import com.example.schoolmanagementsystem.backend.data.remote.SikshaApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: SikshaApiService,
    private val sessionManager: SessionManager,
    private val userDao: UserDao
) : AuthRepository {

    override suspend fun login(email: String, password: String): Resource<User> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.login(LoginRequest(email, password))
            
            if (response.success && response.data != null) {
                val user = response.data.user
                // Excellent Backend: Cache user locally
                userDao.insertUser(user.toEntity())
                sessionManager.saveSession(user.name, user.email, user.role, user.schoolId)
                Resource.Success(user)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Authentication failed")
        }
    }

    override suspend fun logout() = withContext(Dispatchers.IO) {
        try {
            userDao.deleteUser()
            sessionManager.clearSession()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getCurrentUser(): Flow<User?> {
        return userDao.getUser().map { it?.toUser() }.flowOn(Dispatchers.IO)
    }

    override suspend fun signUp(
        email: String,
        password: String,
        role: UserRole,
        fullName: String,
        schoolId: String
    ): Resource<User> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.signUp(
                SignUpRequest(email, password, fullName, role.name, schoolId)
            )
            
            if (response.success && response.data != null) {
                val user = response.data.user
                userDao.insertUser(user.toEntity())
                Resource.Success(user)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Sign up failed")
        }
    }
}

