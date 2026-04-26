package com.example.schoolmanagementsystem.backend.repository

import com.example.schoolmanagementsystem.backend.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, String> {
    fun findByEmail(email: String): User?
}
