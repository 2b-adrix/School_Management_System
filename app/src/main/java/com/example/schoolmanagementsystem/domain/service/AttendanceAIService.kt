package com.example.schoolmanagementsystem.domain.service

import android.graphics.Bitmap
import com.example.schoolmanagementsystem.domain.util.Resource
import java.io.File

interface AttendanceAIService {
    /**
     * Detects and identifies students from a group photo.
     * Returns a list of Student IDs identified.
     */
    suspend fun recognizeStudentsFromImage(bitmap: Bitmap): Resource<List<String>>

    /**
     * Scans a QR code and returns the decoded content (e.g., class session ID).
     */
    suspend fun scanAttendanceQR(bitmap: Bitmap): Resource<String>

    /**
     * Processes a voice recording of a roll call.
     * Uses Gemini AI to extract present student names or IDs.
     */
    suspend fun processVoiceAttendance(audioFile: File, studentList: List<String>): Resource<List<String>>
}
