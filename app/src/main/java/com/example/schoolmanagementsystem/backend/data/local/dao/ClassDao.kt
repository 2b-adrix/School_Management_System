package com.example.schoolmanagementsystem.backend.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.schoolmanagementsystem.backend.data.local.entity.ClassEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClassDao {
    @Query("SELECT * FROM classes")
    fun getAllClasses(): Flow<List<ClassEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClass(schoolClass: ClassEntity): Long

    @Query("SELECT * FROM classes WHERE id = :id")
    suspend fun getClassById(id: String): ClassEntity?
}

