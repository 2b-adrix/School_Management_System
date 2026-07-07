package com.example.schoolmanagementsystem.backend.data.local.dao

import androidx.room.*
import com.example.schoolmanagementsystem.backend.data.local.entity.ExamEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExamDao {
    @Query("SELECT * FROM exams WHERE classId = :classId")
    fun getExamsByClass(classId: String): Flow<List<ExamEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExam(exam: ExamEntity): Long

    @Update
    suspend fun updateExam(exam: ExamEntity): Int

    @Delete
    suspend fun deleteExam(exam: ExamEntity): Int

    @Query("SELECT * FROM exams WHERE id = :id")
    suspend fun getExamById(id: String): ExamEntity?
}
