package com.example.schoolmanagementsystem.domain.service

import com.example.schoolmanagementsystem.domain.util.Resource

interface GenerativeAIService {
    /**
     * Provides an AI insight based on attendance percentage.
     */
    suspend fun getAttendanceInsight(percentage: Float): Resource<String>

    /**
     * Summarizes a subject based on its name and description.
     */
    suspend fun summarizeSubject(name: String, description: String?): Resource<String>

    /**
     * Determines the most important class today based on the timetable.
     */
    suspend fun getImportantClassInsight(timetableEntries: List<String>): Resource<String>

    /**
     * Generates a comprehensive performance report for a class or student.
     */
    suspend fun generatePerformanceReport(data: String): Resource<String>
}
