package com.example.schoolmanagementsystem.backend.domain.repository

import com.example.schoolmanagementsystem.backend.domain.model.Announcement
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface AnnouncementRepository {
    fun getAllAnnouncements(): Flow<Resource<List<Announcement>>>
    fun getAnnouncementsForStudent(classId: String): Flow<Resource<List<Announcement>>>
    suspend fun addAnnouncement(announcement: Announcement): Resource<Unit>
    suspend fun updateAnnouncement(announcement: Announcement): Resource<Unit>
    suspend fun deleteAnnouncement(id: String): Resource<Unit>
}

