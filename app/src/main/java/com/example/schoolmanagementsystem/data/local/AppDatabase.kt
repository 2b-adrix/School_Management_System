package com.example.schoolmanagementsystem.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.schoolmanagementsystem.data.local.dao.*
import com.example.schoolmanagementsystem.data.local.entity.*

@Database(
    entities = [
        StudentEntity::class,
        TeacherEntity::class,
        ClassEntity::class,
        SubjectEntity::class,
        AttendanceEntity::class,
        ExamEntity::class,
        ResultEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
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
