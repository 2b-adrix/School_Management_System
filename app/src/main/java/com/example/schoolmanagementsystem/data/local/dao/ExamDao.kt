package com.example.schoolmanagementsystem.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.schoolmanagementsystem.data.local.entity.ExamEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExamDao {
    @Query("SELECT * FROM exams WHERE classId = :classId")
    fun getExamsByClass(classId: String): Flow<List<ExamEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertExam(exam: ExamEntity): Long

    @Query("SELECT * FROM exams WHERE id = :id")
    fun getExamById(id: String): ExamEntity?
}
