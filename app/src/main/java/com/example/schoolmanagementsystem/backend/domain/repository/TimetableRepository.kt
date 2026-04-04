package com.example.schoolmanagementsystem.backend.domain.repository

import com.example.schoolmanagementsystem.backend.domain.model.TimetableEntry
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface TimetableRepository {
    fun getTimetableForClass(classId: String): Flow<Resource<List<TimetableEntry>>>
    suspend fun addTimetableEntry(entry: TimetableEntry): Resource<Unit>
    suspend fun updateTimetableEntry(entry: TimetableEntry): Resource<Unit>
    suspend fun deleteTimetableEntry(id: String): Resource<Unit>
}

