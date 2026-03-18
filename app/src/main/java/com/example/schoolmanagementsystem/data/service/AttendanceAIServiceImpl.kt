package com.example.schoolmanagementsystem.data.service

import android.content.Context
import android.graphics.Bitmap
import com.example.schoolmanagementsystem.domain.model.Student
import com.example.schoolmanagementsystem.domain.service.AttendanceAIService
import com.example.schoolmanagementsystem.domain.util.Resource
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.tasks.await
import java.io.File
import javax.inject.Inject
import kotlin.math.sqrt

class AttendanceAIServiceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val postgrest: Postgrest
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

    // 3. Gemini Setup (Add your API Key here later)
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = "YOUR_GEMINI_API_KEY"
    )

    override suspend fun recognizeStudentsFromImage(bitmap: Bitmap): Resource<List<String>> {
        return try {
            val image = InputImage.fromBitmap(bitmap, 0)
            val faces = faceDetector.process(image).await()
            
            if (faces.isEmpty()) return Resource.Error("No faces detected")

            // Fetch all students with face embeddings from Supabase
            val studentsResult = postgrest["students"].select().decodeList<Student>()
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

    override suspend fun scanAttendanceQR(bitmap: Bitmap): Resource<String> {
        return try {
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
    ): Resource<List<String>> {
        return try {
            // Convert audio to content for Gemini
            val response = generativeModel.generateContent(
                content {
                    text("The following students are present in the roll call audio. " +
                            "Compare with this list: $studentList and return only the IDs of present students as a comma-separated list.")
                    // blob("audio/mp3", audioFile.readBytes()) // Requires experimental audio support
                }
            )
            
            val ids = response.text?.split(",")?.map { it.trim() } ?: emptyList()
            Resource.Success(ids)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Voice processing failed")
        }
    }
}
