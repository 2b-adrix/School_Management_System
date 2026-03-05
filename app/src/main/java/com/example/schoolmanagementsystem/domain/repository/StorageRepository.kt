package com.example.schoolmanagementsystem.domain.repository

import com.example.schoolmanagementsystem.domain.util.Resource

interface StorageRepository {
    suspend fun uploadProfileImage(
        path: String,
        imageBytes: ByteArray,
        bucketName: String = "profiles"
    ): Resource<String>
    
    fun getPublicUrl(path: String, bucketName: String = "profiles"): String
}
