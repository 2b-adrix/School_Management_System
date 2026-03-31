package com.example.schoolmanagementsystem.data.service

import com.example.schoolmanagementsystem.domain.service.GenerativeAIService
import com.example.schoolmanagementsystem.domain.util.Resource
import com.google.ai.client.generativeai.GenerativeModel
import javax.inject.Inject

class GenerativeAIServiceImpl @Inject constructor() : GenerativeAIService {

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = "YOUR_GEMINI_API_KEY" // Placeholder
    )

    override suspend fun getAttendanceInsight(percentage: Float): Resource<String> {
        return try {
            val prompt = "A student has an attendance of $percentage%. Give a 1-sentence supportive advice or insight about this."
            val response = generativeModel.generateContent(prompt)
            Resource.Success(response.text ?: "Keep up the good work!")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get insight")
        }
    }

    override suspend fun summarizeSubject(name: String, description: String?): Resource<String> {
        return try {
            val prompt = "Summarize the school subject '$name' with description '$description' in 1 short sentence for a student."
            val response = generativeModel.generateContent(prompt)
            Resource.Success(response.text ?: "Learn more about $name.")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to summarize")
        }
    }

    override suspend fun getImportantClassInsight(timetableEntries: List<String>): Resource<String> {
        return try {
            val prompt = "Given today's classes: $timetableEntries. Which one should a student prioritize today? Give a 1-sentence reason."
            val response = generativeModel.generateContent(prompt)
            Resource.Success(response.text ?: "Focus on your upcoming lessons.")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get insight")
        }
    }

    override suspend fun generatePerformanceReport(data: String): Resource<String> {
        return try {
            val prompt = "Generate a comprehensive performance report for this student's data: $data. Provide it in 3 sentences."
            val response = generativeModel.generateContent(prompt)
            Resource.Success(response.text ?: "No report generated.")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to generate report")
        }
    }
}
