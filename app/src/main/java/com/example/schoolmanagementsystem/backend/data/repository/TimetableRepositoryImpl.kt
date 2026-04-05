package com.example.schoolmanagementsystem.backend.data.repository

import com.example.schoolmanagementsystem.backend.data.local.dao.TimetableDao
import com.example.schoolmanagementsystem.backend.data.local.entity.TimetableEntity
import com.example.schoolmanagementsystem.backend.data.manager.SessionManager
import com.example.schoolmanagementsystem.backend.domain.model.TimetableEntry
import com.example.schoolmanagementsystem.backend.domain.repository.TimetableRepository
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class TimetableRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val sessionManager: SessionManager,
    private val timetableDao: TimetableDao
) : TimetableRepository {

    override fun getTimetableForClass(classId: String): Flow<Resource<List<TimetableEntry>>> = flow {
        emit(Resource.Loading())
        
        // Emit local data first
        val localData = timetableDao.getTimetableForClass(classId).first().map { it.toDomain() }
        if (localData.isNotEmpty()) {
            emit(Resource.Success(localData))
        }

        try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            val timetable = postgrest["timetable"]
                .select {
                    filter {
                        eq("school_id", schoolId)
                        eq("class_id", classId)
                    }
                }
                .decodeList<TimetableEntry>()
            
            // Update local cache
            timetableDao.clearTimetableForClass(classId)
            timetableDao.insertAll(timetable.map { it.toEntity() })
            
            emit(Resource.Success(timetable))
        } catch (e: Exception) {
            // If network fails, we've already emitted local data if it existed
            if (localData.isEmpty()) {
                emit(Resource.Error(e.message ?: "An error occurred"))
            }
        }
    }

    override suspend fun addTimetableEntry(entry: TimetableEntry): Resource<Unit> {
        return try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            val entryWithSchoolId = entry.copy(schoolId = schoolId)
            postgrest["timetable"].insert(entryWithSchoolId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add timetable entry")
        }
    }

    override suspend fun updateTimetableEntry(entry: TimetableEntry): Resource<Unit> {
        return try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            postgrest["timetable"].update(entry.copy(schoolId = schoolId)) {
                filter {
                    eq("id", entry.id)
                    eq("school_id", schoolId)
                }
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update timetable entry")
        }
    }

    override suspend fun deleteTimetableEntry(id: String): Resource<Unit> {
        return try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            postgrest["timetable"].delete {
                filter {
                    eq("id", id)
                    eq("school_id", schoolId)
                }
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete timetable entry")
        }
    }

    private fun TimetableEntry.toEntity() = TimetableEntity(
        id = id,
        schoolId = schoolId,
        classId = classId,
        subjectId = subjectId,
        teacherId = teacherId,
        dayOfWeek = dayOfWeek,
        startTime = startTime,
        endTime = endTime,
        roomNumber = roomNumber
    )

    private fun TimetableEntity.toDomain() = TimetableEntry(
        id = id,
        schoolId = schoolId,
        classId = classId,
        subjectId = subjectId,
        teacherId = teacherId,
        dayOfWeek = dayOfWeek,
        startTime = startTime,
        endTime = endTime,
        roomNumber = roomNumber
    )
}
