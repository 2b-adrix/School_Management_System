package com.example.schoolmanagementsystem.data.repository

import com.example.schoolmanagementsystem.data.local.dao.AttendanceDao
import com.example.schoolmanagementsystem.data.local.entity.toDomain
import com.example.schoolmanagementsystem.data.local.entity.toEntity
import com.example.schoolmanagementsystem.data.manager.SessionManager
import com.example.schoolmanagementsystem.domain.model.AttendanceRecord
import com.example.schoolmanagementsystem.domain.repository.AttendanceRepository
import com.example.schoolmanagementsystem.domain.util.Resource
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class AttendanceRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
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
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            val remoteData = postgrest["attendance"]
                .select {
                    filter {
                        eq("school_id", schoolId)
                        eq("class_id", classId)
                        eq("subject_id", subjectId)
                        eq("date", date)
                    }
                }
                .decodeList<AttendanceRecord>()
            
            attendanceDao.insertAttendance(remoteData.map { it.toEntity(isSynced = true) })
            emit(Resource.Success(remoteData))
        } catch (e: Exception) {
            if (localData.isEmpty()) {
                emit(Resource.Error(e.message ?: "An error occurred"))
            }
        }
    }

    override suspend fun saveAttendance(records: List<AttendanceRecord>): Resource<Unit> {
        return try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            val recordsWithSchoolId = records.map { it.copy(schoolId = schoolId) }
            
            // Excellent Backend: Mark as unsynced locally for background worker
            attendanceDao.insertAttendance(recordsWithSchoolId.map { it.toEntity(isSynced = false) })
            
            // Try instant sync
            postgrest["attendance"].upsert(recordsWithSchoolId)
            attendanceDao.markAsSynced(recordsWithSchoolId.map { it.id })
            
            Resource.Success(Unit)
        } catch (e: Exception) {
            // Fails silently - Worker will handle this later
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
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            val remoteData = postgrest["attendance"]
                .select {
                    filter {
                        eq("school_id", schoolId)
                        eq("student_id", studentId)
                    }
                }
                .decodeList<AttendanceRecord>()
            
            attendanceDao.insertAttendance(remoteData.map { it.toEntity(isSynced = true) })
            emit(Resource.Success(remoteData))
        } catch (e: Exception) {
            if (localData.isEmpty()) {
                emit(Resource.Error(e.message ?: "An error occurred"))
            }
        }
    }
}
