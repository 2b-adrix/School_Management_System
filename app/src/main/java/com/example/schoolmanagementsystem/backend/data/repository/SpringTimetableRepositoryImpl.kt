package com.example.schoolmanagementsystem.backend.data.repository

import com.example.schoolmanagementsystem.backend.data.remote.SikshaApiService
import com.example.schoolmanagementsystem.backend.domain.model.TimetableEntry
import com.example.schoolmanagementsystem.backend.domain.repository.TimetableRepository
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SpringTimetableRepositoryImpl @Inject constructor(
    private val apiService: SikshaApiService
) : TimetableRepository {

    override fun getTimetableForClass(classId: String): Flow<Resource<List<TimetableEntry>>> = flow {
        emit(Resource.Loading())
        try {
            // For now, fetching all and filtering by classId if needed, 
            // or we can add a specific endpoint in Spring Boot later.
            val entries = apiService.getAllTimetableEntries().filter { it.classId == classId }
            emit(Resource.Success(entries))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch timetable from SIKSHA Backend"))
        }
    }

    override suspend fun addTimetableEntry(entry: TimetableEntry): Resource<Unit> {
        return try {
            apiService.saveTimetableEntry(entry)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add timetable entry")
        }
    }

    override suspend fun updateTimetableEntry(entry: TimetableEntry): Resource<Unit> {
        // Spring Boot save works as update if ID is provided
        return addTimetableEntry(entry)
    }

    override suspend fun deleteTimetableEntry(id: String): Resource<Unit> {
        // To be implemented in Spring Boot
        return Resource.Error("Delete not yet supported on backend")
    }
}
