package com.example.schoolmanagementsystem.backend.data.repository

import com.example.schoolmanagementsystem.backend.data.manager.SessionManager
import com.example.schoolmanagementsystem.backend.data.remote.SikshaApiService
import com.example.schoolmanagementsystem.backend.domain.model.Announcement
import com.example.schoolmanagementsystem.backend.domain.repository.AnnouncementRepository
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AnnouncementRepositoryImpl @Inject constructor(
    private val apiService: SikshaApiService,
    private val sessionManager: SessionManager
) : AnnouncementRepository {

    override fun getAllAnnouncements(): Flow<Resource<List<Announcement>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getAllAnnouncements()
            if (response.success && response.data != null) {
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }.flowOn(Dispatchers.IO)

    override fun getAnnouncementsForStudent(studentId: String): Flow<Resource<List<Announcement>>> = flow {
        emit(Resource.Loading())
        try {
            // For now, use the same as getAll, or filter by schoolId in backend
            val response = apiService.getAllAnnouncements()
            if (response.success && response.data != null) {
                emit(Resource.Success(response.data))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun addAnnouncement(announcement: Announcement): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.createAnnouncement(announcement)
            if (response.success) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add announcement")
        }
    }

    override suspend fun updateAnnouncement(announcement: Announcement): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.updateAnnouncement(announcement.id, announcement)
            if (response.success) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update announcement")
        }
    }

    override suspend fun deleteAnnouncement(id: String): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.deleteAnnouncement(id)
            if (response.success) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete announcement")
        }
    }
}
