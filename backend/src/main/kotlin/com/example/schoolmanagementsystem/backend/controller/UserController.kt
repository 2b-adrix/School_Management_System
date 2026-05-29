package com.example.schoolmanagementsystem.backend.controller

import com.example.schoolmanagementsystem.backend.model.User
import com.example.schoolmanagementsystem.backend.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/users")
class UserController(private val userService: UserService) {

    @GetMapping("/me")
    fun getCurrentUser(): ResponseEntity<Any> {
        val email = SecurityContextHolder.getContext().authentication.name
        return ResponseEntity.ok(userService.getFullProfile(email))
    }

    @PostMapping("/change-password")
    fun changePassword(@RequestBody request: ChangePasswordRequest): ResponseEntity<Map<String, String>> {
        val email = SecurityContextHolder.getContext().authentication.name
        userService.changePassword(email, request.oldPassword, request.newPassword)
        return ResponseEntity.ok(mapOf("message" to "Password changed successfully"))
    }

    @PutMapping("/profile")
    fun updateProfile(@RequestBody request: UpdateProfileRequest): ResponseEntity<User> {
        val email = SecurityContextHolder.getContext().authentication.name
        return ResponseEntity.ok(userService.updateProfile(email, request))
    }
}

data class ChangePasswordRequest(val oldPassword: String, val newPassword: String)
data class UpdateProfileRequest(
    val name: String? = null,
    val phoneNumber: String? = null,
    val photoUrl: String? = null,
    val qualification: String? = null
)
