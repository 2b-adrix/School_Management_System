package com.example.schoolmanagementsystem.backend.data.service

import android.graphics.Bitmap
import com.example.schoolmanagementsystem.BuildConfig
import com.example.schoolmanagementsystem.backend.data.model.*
import com.example.schoolmanagementsystem.backend.domain.service.AttendanceAIService
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class AttendanceAIServiceImpl @Inject constructor(
    private val client: HttpClient
) : AttendanceAIService {

    // 1. Face Detection Setup
    private val faceDetector = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()
    )

    // 2. QR Scanner Setup
    private val qrScanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
    )

    // 3. Gemini REST Setup
    private val apiKey = BuildConfig.GEMINI_API_KEY
    private val model = "gemini-1.5-flash"

    override suspend fun recognizeStudentsFromImage(bitmap: Bitmap): Resource<List<String>> = withContext(Dispatchers.IO) {
        try {
            val image = InputImage.fromBitmap(bitmap, 0)
            val faces = faceDetector.process(image).await()
            
            if (faces.isEmpty()) return@withContext Resource.Error("No faces detected")

            // Fetch all students with face embeddings from Supabase
            // val studentsResult = postgrest["students"].select().decodeList<Student>()
            val identifiedIds = mutableListOf<String>()

            // Simple Euclidean distance for face matching (Placeholder logic)
            // In a production app, you'd use a TFLite model to generate embeddings from detected faces
            // and compare them with stored embeddings.
            
            // For now, this is the structural implementation
            identifiedIds.add("example_student_id") 

            Resource.Success(identifiedIds)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Recognition failed")
        }
    }

    override suspend fun scanAttendanceQR(bitmap: Bitmap): Resource<String> = withContext(Dispatchers.IO) {
        try {
            val image = InputImage.fromBitmap(bitmap, 0)
            val barcodes = qrScanner.process(image).await()
            val qrCode = barcodes.firstOrNull { it.format == Barcode.FORMAT_QR_CODE }
            
            qrCode?.rawValue?.let {
                Resource.Success(it)
            } ?: Resource.Error("No QR code found")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "QR Scan failed")
        }
    }

    override suspend fun processVoiceAttendance(
        audioFile: File,
        studentList: List<String>
    ): Resource<List<String>> = withContext(Dispatchers.IO) {
        try {
            val audioBytes = audioFile.readBytes()
            val base64Audio = android.util.Base64.encodeToString(audioBytes, android.util.Base64.NO_WRAP)
            
            val prompt = "Identify which students from this list are mentioned as 'present' in the audio: $studentList. Return only their IDs as a comma-separated list."

            val response: GeminiResponse = client.post("${model}:generateContent") {
                parameter("key", apiKey)
                setBody(GeminiRequest(
                    contents = listOf(Content(parts = listOf(
                        Part(text = prompt),
                        Part(inlineData = InlineData(mimeType = "audio/wav", data = base64Audio))
                    )))
                ))
                contentType(ContentType.Application.Json)
            }.body()

            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            val ids = text?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()
            Resource.Success(ids)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Voice processing failed")
        }
    }
}

