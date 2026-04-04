package com.example.schoolmanagementsystem.backend.data.repository

import com.example.schoolmanagementsystem.backend.data.local.dao.UserDao
import com.example.schoolmanagementsystem.backend.data.local.entity.toEntity
import com.example.schoolmanagementsystem.backend.data.manager.SessionManager
import com.example.schoolmanagementsystem.backend.domain.model.User
import com.example.schoolmanagementsystem.backend.domain.model.UserRole
import com.example.schoolmanagementsystem.backend.domain.repository.AuthRepository
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val supabaseAuth: Auth,
    private val sessionManager: SessionManager,
    private val userDao: UserDao
) : AuthRepository {

    override suspend fun login(email: String, password: String): Resource<User> = withContext(Dispatchers.IO) {
        try {
            supabaseAuth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            val currentUser = supabaseAuth.currentUserOrNull()
            if (currentUser != null) {
                val roleString = currentUser.userMetadata?.get("role")?.jsonPrimitive?.content?.uppercase() ?: "STUDENT"
                val role = try {
                    UserRole.valueOf(roleString)
                } catch (e: Exception) {
                    UserRole.STUDENT
                }
                
                val schoolId = currentUser.userMetadata?.get("school_id")?.jsonPrimitive?.content ?: ""
                val name = currentUser.userMetadata?.get("full_name")?.jsonPrimitive?.content ?: "User"

                val user = User(
                    id = currentUser.id,
                    name = name,
                    email = currentUser.email ?: email,
                    role = role,
                    schoolId = schoolId
                )

                // Excellent Backend: Cache user locally
                userDao.insertUser(user.toEntity())
                sessionManager.saveSession(name, user.email, role, schoolId)
                
                Resource.Success(user)
            } else {
                Resource.Error("Login failed")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Authentication failed")
        }
    }

    override suspend fun logout() = withContext(Dispatchers.IO) {
        try {
            supabaseAuth.signOut()
            userDao.deleteUser()
            sessionManager.clearSession()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getCurrentUser(): Flow<User?> {
        // Excellent Backend: Reactive flow from local cache
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
            supabaseAuth.signUpWith(Email) {
                this.email = email
                this.password = password
                data = buildJsonObject {
                    put("full_name", fullName)
                    put("role", role.name)
                    put("school_id", schoolId)
                }
            }
            val user = supabaseAuth.currentUserOrNull()
            if (user != null) {
                val newUser = User(
                    id = user.id,
                    name = fullName,
                    email = email,
                    role = role,
                    schoolId = schoolId
                )
                userDao.insertUser(newUser.toEntity())
                Resource.Success(newUser)
            } else {
                Resource.Error("Signup successful but user not found")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Sign up failed")
        }
    }
}

