package com.example.schoolmanagementsystem.domain.repository

import com.example.schoolmanagementsystem.domain.model.AttendanceRecord
import com.example.schoolmanagementsystem.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface AttendanceRepository {
    fun getAttendanceForClassSubject(classId: String, subjectId: String, date: String): Flow<Resource<List<AttendanceRecord>>>
    suspend fun saveAttendance(records: List<AttendanceRecord>): Resource<Unit>
    fun getAttendanceForStudent(studentId: String): Flow<Resource<List<AttendanceRecord>>>
}
