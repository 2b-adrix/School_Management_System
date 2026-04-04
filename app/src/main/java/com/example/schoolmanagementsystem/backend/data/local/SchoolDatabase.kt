package com.example.schoolmanagementsystem.backend.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.schoolmanagementsystem.backend.data.local.dao.UserDao
import com.example.schoolmanagementsystem.backend.data.local.entity.UserEntity

@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SchoolDatabase : RoomDatabase() {
    abstract val userDao: UserDao
}

