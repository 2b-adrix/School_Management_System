package com.example.schoolmanagementsystem.data.repository

import com.example.schoolmanagementsystem.domain.model.TimetableEntry
import com.example.schoolmanagementsystem.domain.repository.TimetableRepository
import com.example.schoolmanagementsystem.domain.util.Resource
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TimetableRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest
) : TimetableRepository {

    override fun getTimetableForClass(classId: String): Flow<Resource<List<TimetableEntry>>> = flow {
        emit(Resource.Loading())
        try {
            val timetable = postgrest["timetable"]
                .select {
                    filter {
                        eq("classId", classId)
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
            postgrest["timetable"].insert(entry)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add timetable entry")
        }
    }

    override suspend fun updateTimetableEntry(entry: TimetableEntry): Resource<Unit> {
        return try {
            postgrest["timetable"].update(entry) {
                filter {
                    eq("id", entry.id)
                }
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update timetable entry")
        }
    }

    override suspend fun deleteTimetableEntry(id: String): Resource<Unit> {
        return try {
            postgrest["timetable"].delete {
                filter {
                    eq("id", id)
                }
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete timetable entry")
        }
    }
}
