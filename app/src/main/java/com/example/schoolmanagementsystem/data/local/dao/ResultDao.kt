package com.example.schoolmanagementsystem.data.local.dao

import androidx.room.*
import com.example.schoolmanagementsystem.data.local.entity.ResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ResultDao {
    @Query("SELECT * FROM results WHERE examId = :examId")
    fun getResultsByExam(examId: String): Flow<List<ResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertResult(result: ResultEntity): Long

    @Query("SELECT * FROM results WHERE studentId = :studentId")
    fun getResultsByStudent(studentId: String): Flow<List<ResultEntity>>
}
