package com.example.schoolmanagementsystem.backend.data.repository

import com.example.schoolmanagementsystem.backend.data.manager.SessionManager
import com.example.schoolmanagementsystem.backend.domain.model.TimetableEntry
import com.example.schoolmanagementsystem.backend.domain.repository.TimetableRepository
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TimetableRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val sessionManager: SessionManager
) : TimetableRepository {

    override fun getTimetableForClass(classId: String): Flow<Resource<List<TimetableEntry>>> = flow {
        emit(Resource.Loading())
        try {
            val schoolId = sessionManager.schoolId.firstOrNull()
            val timetable = postgrest["timetable"]
                .select {
                    filter {
                        eq("school_id", schoolId ?: "")
                        eq("class_id", classId)
                    }
                }
                .decodeList<TimetableEntry>()
            emit(Resource.Success(timetable))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
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
}

