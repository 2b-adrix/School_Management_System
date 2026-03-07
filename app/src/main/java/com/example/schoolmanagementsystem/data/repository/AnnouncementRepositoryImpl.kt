package com.example.schoolmanagementsystem.data.repository

import com.example.schoolmanagementsystem.data.manager.SessionManager
import com.example.schoolmanagementsystem.domain.model.Announcement
import com.example.schoolmanagementsystem.domain.repository.AnnouncementRepository
import com.example.schoolmanagementsystem.domain.util.Resource
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
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
                        eq("schoolId", schoolId ?: "")
                    }
                }
                .decodeList<Announcement>()
            emit(Resource.Success(announcements))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    override fun getAnnouncementsForStudent(classId: String): Flow<Resource<List<Announcement>>> = flow {
        emit(Resource.Loading())
        try {
            val schoolId = sessionManager.schoolId.firstOrNull()
            // Fetch announcements targeted to ALL, STUDENT, or the specific class within the school
            val announcements = postgrest["announcements"]
                .select {
                    filter {
                        eq("schoolId", schoolId ?: "")
                        or {
                            eq("targetRole", "ALL")
                            eq("targetRole", "STUDENT")
                            eq("targetClassId", classId)
                        }
                    }
                }
                .decodeList<Announcement>()
            emit(Resource.Success(announcements))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    override suspend fun addAnnouncement(announcement: Announcement): Resource<Unit> {
        return try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            val announcementWithSchoolId = announcement.copy(schoolId = schoolId)
            postgrest["announcements"].insert(announcementWithSchoolId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add announcement")
        }
    }

    override suspend fun updateAnnouncement(announcement: Announcement): Resource<Unit> {
        return try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            postgrest["announcements"].update(announcement.copy(schoolId = schoolId)) {
                filter {
                    eq("id", announcement.id)
                    eq("schoolId", schoolId)
                }
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update announcement")
        }
    }

    override suspend fun deleteAnnouncement(id: String): Resource<Unit> {
        return try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            postgrest["announcements"].delete {
                filter {
                    eq("id", id)
                    eq("schoolId", schoolId)
                }
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete announcement")
        }
    }
}
