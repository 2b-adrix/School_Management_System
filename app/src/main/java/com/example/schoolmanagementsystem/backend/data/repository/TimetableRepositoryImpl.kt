package com.example.schoolmanagementsystem.backend.data.repository

import com.example.schoolmanagementsystem.backend.data.local.dao.TimetableDao
import com.example.schoolmanagementsystem.backend.data.local.entity.TimetableEntity
import com.example.schoolmanagementsystem.backend.data.manager.SessionManager
import com.example.schoolmanagementsystem.backend.data.remote.SikshaApiService
import com.example.schoolmanagementsystem.backend.domain.model.TimetableEntry
import com.example.schoolmanagementsystem.backend.domain.repository.TimetableRepository
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TimetableRepositoryImpl @Inject constructor(
    private val apiService: SikshaApiService,
    private val sessionManager: SessionManager,
    private val timetableDao: TimetableDao
) : TimetableRepository {

    override fun getTimetableForClass(classId: String): Flow<Resource<List<TimetableEntry>>> = flow {
        // Emit local data immediately
        val localData = timetableDao.getTimetableForClass(classId).first()
        if (localData.isNotEmpty()) {
            emit(Resource.Success(localData.map { it.toDomain() }))
        } else {
            emit(Resource.Loading())
        }

        try {
            val response = apiService.getTimetableForClass(classId)
            if (response.success && response.data != null) {
                val remoteData = response.data
                timetableDao.insertTimetable(remoteData.map { it.toEntity() })
                emit(Resource.Success(remoteData))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: Exception) {
            if (localData.isEmpty()) {
                emit(Resource.Error(e.message ?: "An error occurred"))
            }
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun addTimetableEntry(entry: TimetableEntry): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.addTimetableEntry(entry)
            if (response.success) {
                timetableDao.insertTimetable(listOf(entry.toEntity()))
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add timetable entry")
        }
    }

    override suspend fun updateTimetableEntry(entry: TimetableEntry): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            // Need update endpoint in SikshaApiService if not there
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update timetable entry")
        }
    }

    override suspend fun deleteTimetableEntry(id: String): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.deleteTimetableEntry(id)
            if (response.success) {
                // Need delete in DAO
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete timetable entry")
        }
    }

    private fun TimetableEntry.toEntity() = TimetableEntity(
        id = id,
        classId = classId,
        subjectId = subjectId,
        subjectName = subjectName,
        teacherId = teacherId,
        teacherName = teacherName,
        dayOfWeek = dayOfWeek,
        startTime = startTime,
        endTime = endTime,
        roomNumber = roomNumber,
        schoolId = schoolId
    )

    private fun TimetableEntity.toDomain() = TimetableEntry(
        id = id,
        classId = classId,
        subjectId = subjectId,
        subjectName = subjectName,
        teacherId = teacherId,
        teacherName = teacherName,
        dayOfWeek = dayOfWeek,
        startTime = startTime,
        endTime = endTime,
        roomNumber = roomNumber,
        schoolId = schoolId
    )
}
