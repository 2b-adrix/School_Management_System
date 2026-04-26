package com.example.schoolmanagementsystem.backend.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class GeminiService(
    @Value("\${gemini.api.key}") private val apiKey: String
) {
    private val restTemplate = RestTemplate()
    private val model = "gemini-1.5-flash"
    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta/models"

    fun getAttendanceInsight(percentage: Float): String {
        val prompt = "A student has an attendance of \$percentage%. Give a 1-sentence supportive advice or insight about this."
        return generateText(prompt)
    }

    private fun generateText(prompt: String): String {
        val url = "\$baseUrl/\$model:generateContent?key=\$apiKey"
        val requestBody = mapOf(
            "contents" to listOf(
                mapOf("parts" to listOf(mapOf("text" to prompt)))
            )
        )
        
        return try {
            val response = restTemplate.postForObject(url, requestBody, Map::class.java)
            // Note: In production, use a proper DTO for parsing
            val candidates = response?.get("candidates") as? List<Map<String, Any>>
            val content = candidates?.firstOrNull()?.get("content") as? Map<String, Any>
            val parts = content?.get("parts") as? List<Map<String, Any>>
            parts?.firstOrNull()?.get("text") as? String ?: "No insight available"
        } catch (e: Exception) {
            "AI Service currently unavailable"
        }
    }
}
