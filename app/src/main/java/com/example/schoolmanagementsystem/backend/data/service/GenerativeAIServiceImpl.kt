package com.example.schoolmanagementsystem.backend.data.service

import com.example.schoolmanagementsystem.BuildConfig
import com.example.schoolmanagementsystem.backend.data.model.*
import com.example.schoolmanagementsystem.backend.domain.service.GenerativeAIService
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import javax.inject.Inject

class GenerativeAIServiceImpl @Inject constructor(
    private val client: HttpClient
) : GenerativeAIService {

    private val apiKey = BuildConfig.GEMINI_API_KEY
    private val model = "gemini-1.5-flash"

    private suspend fun generateText(prompt: String): Resource<String> {
        return try {
            val response: GeminiResponse = client.post("${model}:generateContent") {
                parameter("key", apiKey)
                setBody(GeminiRequest(
                    contents = listOf(Content(parts = listOf(Part(text = prompt))))
                ))
                contentType(ContentType.Application.Json)
            }.body()

            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            Resource.Success(text ?: "No response from AI")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "AI request failed")
        }
    }

    override suspend fun getAttendanceInsight(percentage: Float): Resource<String> {
        val prompt = "A student has an attendance of $percentage%. Give a 1-sentence supportive advice or insight about this."
        return generateText(prompt)
    }

    override suspend fun summarizeSubject(name: String, description: String?): Resource<String> {
        val prompt = "Summarize the school subject '$name' with description '$description' in 1 short sentence for a student."
        return generateText(prompt)
    }

    override suspend fun getImportantClassInsight(timetableEntries: List<String>): Resource<String> {
        val prompt = "Given today's classes: $timetableEntries. Which one should a student prioritize today? Give a 1-sentence reason."
        return generateText(prompt)
    }

    override suspend fun generatePerformanceReport(data: String): Resource<String> {
        val prompt = "Generate a comprehensive performance report for this student's data: $data. Provide it in 3 sentences."
        return generateText(prompt)
    }
}

