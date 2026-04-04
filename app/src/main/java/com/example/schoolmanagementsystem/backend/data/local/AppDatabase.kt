package com.example.schoolmanagementsystem.backend.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.schoolmanagementsystem.backend.data.local.dao.*
import com.example.schoolmanagementsystem.backend.data.local.entity.*

@Database(
    entities = [
        UserEntity::class,
        StudentEntity::class,
        TeacherEntity::class,
        ClassEntity::class,
        SubjectEntity::class,
        AttendanceEntity::class,
        ExamEntity::class,
        ResultEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun studentDao(): StudentDao
    abstract fun teacherDao(): TeacherDao
    abstract fun classDao(): ClassDao
    abstract fun subjectDao(): SubjectDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun examDao(): ExamDao
    abstract fun resultDao(): ResultDao

    companion object {
        const val DATABASE_NAME = "school_db"
    }
}

