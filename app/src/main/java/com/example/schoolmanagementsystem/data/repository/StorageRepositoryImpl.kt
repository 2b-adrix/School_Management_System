package com.example.schoolmanagementsystem.data.repository

import com.example.schoolmanagementsystem.domain.repository.StorageRepository
import com.example.schoolmanagementsystem.domain.util.Resource
import io.github.jan.supabase.storage.Storage
import javax.inject.Inject

class StorageRepositoryImpl @Inject constructor(
    private val supabaseStorage: Storage
) : StorageRepository {

    override suspend fun uploadProfileImage(
        path: String,
        imageBytes: ByteArray,
        bucketName: String
    ): Resource<String> {
        return try {
            val bucket = supabaseStorage.from(bucketName)
            bucket.upload(path, imageBytes, upsert = true)
            Resource.Success(path)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to upload image")
        }
    }

    override fun getPublicUrl(path: String, bucketName: String): String {
        val bucket = supabaseStorage.from(bucketName)
        return bucket.publicUrl(path)
    }
}
