package com.example.schoolmanagementsystem.backend.data.local.dao

import androidx.room.*
import com.example.schoolmanagementsystem.backend.data.local.entity.TeacherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TeacherDao {
    @Query("SELECT * FROM teachers")
    fun getAllTeachers(): Flow<List<TeacherEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeacher(teacher: TeacherEntity): Long

    @Update
    suspend fun updateTeacher(teacher: TeacherEntity): Int

    @Delete
    suspend fun deleteTeacher(teacher: TeacherEntity): Int

    @Query("SELECT * FROM teachers WHERE id = :id")
    suspend fun getTeacherById(id: String): TeacherEntity?
}
