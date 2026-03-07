package com.example.schoolmanagementsystem.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.schoolmanagementsystem.data.local.entity.StudentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Query("SELECT * FROM students")
    fun getAllStudents(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE className = :className")
    fun getStudentsByClass(className: String): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE id = :id")
    fun getStudentById(id: String): StudentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStudent(student: StudentEntity): Long

    @Update
    fun updateStudent(student: StudentEntity): Int

    @Delete
    fun deleteStudent(student: StudentEntity): Int
}
