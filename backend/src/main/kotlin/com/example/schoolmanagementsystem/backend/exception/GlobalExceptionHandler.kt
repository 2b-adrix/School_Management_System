package com.example.schoolmanagementsystem.backend.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.time.LocalDateTime

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFound(ex: ResourceNotFoundException): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            status = HttpStatus.NOT_FOUND.value(),
            message = ex.message ?: "Resource not found",
            timestamp = LocalDateTime.now()
        )
        return ResponseEntity(error, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralException(ex: Exception): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            message = ex.message ?: "An unexpected error occurred",
            timestamp = LocalDateTime.now()
        )
        return ResponseEntity(error, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}

class ResourceNotFoundException(message: String) : RuntimeException(message)

data class ErrorResponse(
    val status: Int,
    val message: String,
    val timestamp: LocalDateTime
)
