package com.example.schoolmanagementsystem.backend.data.repository

import com.example.schoolmanagementsystem.backend.domain.repository.StorageRepository
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import javax.inject.Inject

class StorageRepositoryImpl @Inject constructor() : StorageRepository {

    override suspend fun uploadProfileImage(
        bucketName: String,
        bytes: ByteArray,
        fileName: String
    ): Resource<String> {
        // Placeholder until backend storage is implemented
        return Resource.Success("https://via.placeholder.com/150")
    }

    override fun getPublicUrl(bucketName: String, path: String): String {
        return "https://via.placeholder.com/150"
    }
}
