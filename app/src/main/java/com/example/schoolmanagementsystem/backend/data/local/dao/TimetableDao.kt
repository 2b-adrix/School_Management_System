package com.example.schoolmanagementsystem.backend.data.local.dao

import androidx.room.*
import com.example.schoolmanagementsystem.backend.data.local.entity.TimetableEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TimetableDao {
    @Query("SELECT * FROM timetable WHERE classId = :classId ORDER BY dayOfWeek, startTime")
    fun getTimetableForClass(classId: String): Flow<List<TimetableEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(timetable: List<TimetableEntity>)

    @Query("DELETE FROM timetable WHERE classId = :classId")
    suspend fun clearTimetableForClass(classId: String)
}
