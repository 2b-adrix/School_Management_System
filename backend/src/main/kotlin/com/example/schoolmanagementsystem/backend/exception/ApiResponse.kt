package com.example.schoolmanagementsystem.backend.exception

import java.time.LocalDateTime

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null,
    val timestamp: String = LocalDateTime.now().toString()
) {
    companion object {
        fun <T> success(data: T, message: String = "Success"): ApiResponse<T> =
            ApiResponse(success = true, message = message, data = data)

        fun <T> error(message: String): ApiResponse<T> =
            ApiResponse(success = false, message = message)
    }
}
