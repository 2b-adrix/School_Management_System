package com.example.schoolmanagementsystem.data.repository

import com.example.schoolmanagementsystem.domain.model.User
import com.example.schoolmanagementsystem.domain.model.UserRole
import com.example.schoolmanagementsystem.domain.repository.AuthRepository
import com.example.schoolmanagementsystem.domain.util.Resource
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val supabaseAuth: Auth
) : AuthRepository {

    override suspend fun login(email: String, password: String): Resource<User> {
        return try {
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
                
                Resource.Success(
                    User(
                        id = currentUser.id,
                        name = currentUser.userMetadata?.get("full_name")?.jsonPrimitive?.content ?: "User",
                        email = currentUser.email ?: email,
                        role = role,
                        schoolId = schoolId
                    )
                )
            } else {
                Resource.Error("Login failed")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Authentication failed")
        }
    }

    override suspend fun logout() {
        try {
            supabaseAuth.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getCurrentUser(): Flow<User?> {
        return supabaseAuth.sessionStatus.map { status ->
            if (status is SessionStatus.Authenticated) {
                supabaseAuth.currentUserOrNull()?.let { user ->
                    val roleString = user.userMetadata?.get("role")?.jsonPrimitive?.content?.uppercase() ?: "STUDENT"
                    val role = try {
                        UserRole.valueOf(roleString)
                    } catch (e: Exception) {
                        UserRole.STUDENT
                    }
                    
                    val schoolId = user.userMetadata?.get("school_id")?.jsonPrimitive?.content ?: ""
                    
                    User(
                        id = user.id,
                        name = user.userMetadata?.get("full_name")?.jsonPrimitive?.content ?: "User",
                        email = user.email ?: "",
                        role = role,
                        schoolId = schoolId
                    )
                }
            } else {
                null
            }
        }
    }

    override suspend fun signUp(
        email: String,
        password: String,
        role: UserRole,
        fullName: String,
        schoolId: String
    ): Resource<User> {
        return try {
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
                Resource.Success(
                    User(
                        id = user.id,
                        name = fullName,
                        email = email,
                        role = role,
                        schoolId = schoolId
                    )
                )
            } else {
                Resource.Error("Signup successful but user not found")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Sign up failed")
        }
    }
}
