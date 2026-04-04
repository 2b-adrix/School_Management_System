package com.example.schoolmanagementsystem.data.repository

import com.example.schoolmanagementsystem.data.manager.SessionManager
import com.example.schoolmanagementsystem.domain.model.Announcement
import com.example.schoolmanagementsystem.domain.repository.AnnouncementRepository
import com.example.schoolmanagementsystem.domain.util.Resource
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AnnouncementRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val sessionManager: SessionManager
) : AnnouncementRepository {

    override fun getAllAnnouncements(): Flow<Resource<List<Announcement>>> = flow {
        emit(Resource.Loading())
        try {
            val schoolId = sessionManager.schoolId.firstOrNull()
            val announcements = postgrest["announcements"]
                .select {
                    filter {
                        eq("school_id", schoolId ?: "")
                    }
                }
                .decodeList<Announcement>()
            emit(Resource.Success(announcements))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }.flowOn(Dispatchers.IO)

    override fun getAnnouncementsForStudent(classId: String): Flow<Resource<List<Announcement>>> = flow {
        emit(Resource.Loading())
        try {
            val schoolId = sessionManager.schoolId.firstOrNull()
            // Fetch announcements targeted to ALL, STUDENT, or the specific class within the school
            val announcements = postgrest["announcements"]
                .select {
                    filter {
                        eq("school_id", schoolId ?: "")
                        or {
                            eq("target_role", "ALL")
                            eq("target_role", "STUDENT")
                            eq("target_id", classId)
                        }
                    }
                }
                .decodeList<Announcement>()
            emit(Resource.Success(announcements))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun addAnnouncement(announcement: Announcement): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            val announcementWithSchoolId = announcement.copy(schoolId = schoolId)
            postgrest["announcements"].insert(announcementWithSchoolId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add announcement")
        }
    }

    override suspend fun updateAnnouncement(announcement: Announcement): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            postgrest["announcements"].update(announcement.copy(schoolId = schoolId)) {
                filter {
                    eq("id", announcement.id)
                    eq("school_id", schoolId)
                }
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update announcement")
        }
    }

    override suspend fun deleteAnnouncement(id: String): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            postgrest["announcements"].delete {
                filter {
                    eq("id", id)
                    eq("school_id", schoolId)
                }
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete announcement")
        }
    }
}
