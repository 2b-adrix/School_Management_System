package com.example.schoolmanagementsystem.backend.data.repository

import com.example.schoolmanagementsystem.backend.data.local.dao.AttendanceDao
import com.example.schoolmanagementsystem.backend.data.local.entity.toDomain
import com.example.schoolmanagementsystem.backend.data.local.entity.toEntity
import com.example.schoolmanagementsystem.backend.data.manager.SessionManager
import com.example.schoolmanagementsystem.backend.data.remote.SikshaApiService
import com.example.schoolmanagementsystem.backend.domain.model.AttendanceRecord
import com.example.schoolmanagementsystem.backend.domain.repository.AttendanceRepository
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AttendanceRepositoryImpl @Inject constructor(
    private val apiService: SikshaApiService,
    private val sessionManager: SessionManager,
    private val attendanceDao: AttendanceDao
) : AttendanceRepository {

    override fun getAttendanceForClassSubject(classId: String, subjectId: String, date: String): Flow<Resource<List<AttendanceRecord>>> = flow {
        val localData = attendanceDao.getAttendanceForClass(classId, date).first()
        if (localData.isNotEmpty()) {
            emit(Resource.Success(localData.map { it.toDomain() }))
        } else {
            emit(Resource.Loading())
        }

        try {
            val response = apiService.getClassAttendance(classId, date)
            if (response.success && response.data != null) {
                val remoteData = response.data
                attendanceDao.insertAttendance(remoteData.map { it.toEntity(isSynced = true) })
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

    override suspend fun saveAttendance(records: List<AttendanceRecord>): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            // Save locally first
            attendanceDao.insertAttendance(records.map { it.toEntity(isSynced = false) })
            
            // Try instant sync for each record (or add bulk endpoint to backend)
            var allSuccess = true
            records.forEach { record ->
                val response = apiService.markAttendance(record)
                if (response.success) {
                    attendanceDao.markAsSynced(listOf(record.id))
                } else {
                    allSuccess = false
                }
            }
            
            if (allSuccess) Resource.Success(Unit) else Resource.Error("Some records failed to sync")
        } catch (e: Exception) {
            // Worker will handle background sync
            Resource.Success(Unit) 
        }
    }

    override fun getAttendanceForStudent(studentId: String): Flow<Resource<List<AttendanceRecord>>> = flow {
        val localData = attendanceDao.getAttendanceForStudent(studentId).first()
        if (localData.isNotEmpty()) {
            emit(Resource.Success(localData.map { it.toDomain() }))
        } else {
            emit(Resource.Loading())
        }

        try {
            val response = apiService.getStudentAttendance(studentId)
            if (response.success && response.data != null) {
                val remoteData = response.data
                attendanceDao.insertAttendance(remoteData.map { it.toEntity(isSynced = true) })
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
}
