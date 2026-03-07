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
                val roleString = currentUser.userMetadata?.get("role")?.toString()?.uppercase() ?: "STUDENT"
                val role = try {
                    UserRole.valueOf(roleString)
                } catch (e: Exception) {
                    UserRole.STUDENT
                }
                
                val schoolId = currentUser.userMetadata?.get("school_id")?.toString() ?: ""
                
                Resource.Success(
                    User(
                        id = currentUser.id,
                        name = currentUser.userMetadata?.get("full_name")?.toString() ?: "User",
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
                    val roleString = user.userMetadata?.get("role")?.toString()?.uppercase() ?: "STUDENT"
                    val role = try {
                        UserRole.valueOf(roleString)
                    } catch (e: Exception) {
                        UserRole.STUDENT
                    }
                    
                    val schoolId = user.userMetadata?.get("school_id")?.toString() ?: ""
                    
                    User(
                        id = user.id,
                        name = user.userMetadata?.get("full_name")?.toString() ?: "User",
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
}
