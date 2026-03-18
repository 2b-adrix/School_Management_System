package com.example.schoolmanagementsystem.data.repository

import com.example.schoolmanagementsystem.data.manager.SessionManager
import com.example.schoolmanagementsystem.domain.model.AttendanceRecord
import com.example.schoolmanagementsystem.domain.repository.AttendanceRepository
import com.example.schoolmanagementsystem.domain.util.Resource
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AttendanceRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val sessionManager: SessionManager
) : AttendanceRepository {

    override fun getAttendanceForClassSubject(classId: String, subjectId: String, date: String): Flow<Resource<List<AttendanceRecord>>> = flow {
        emit(Resource.Loading())
        try {
            val schoolId = sessionManager.schoolId.firstOrNull()
            val attendance = postgrest["attendance"]
                .select {
                    filter {
                        eq("school_id", schoolId ?: "")
                        eq("class_id", classId)
                        eq("subject_id", subjectId)
                        eq("date", date)
                    }
                }
                .decodeList<AttendanceRecord>()
            emit(Resource.Success(attendance))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    override suspend fun saveAttendance(records: List<AttendanceRecord>): Resource<Unit> {
        return try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            val recordsWithSchoolId = records.map { it.copy(schoolId = schoolId) }
            postgrest["attendance"].upsert(recordsWithSchoolId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to save attendance")
        }
    }

    override fun getAttendanceForStudent(studentId: String): Flow<Resource<List<AttendanceRecord>>> = flow {
        emit(Resource.Loading())
        try {
            val schoolId = sessionManager.schoolId.firstOrNull()
            val attendance = postgrest["attendance"]
                .select {
                    filter {
                        eq("school_id", schoolId ?: "")
                        eq("student_id", studentId)
                    }
                }
                .decodeList<AttendanceRecord>()
            emit(Resource.Success(attendance))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }
}
