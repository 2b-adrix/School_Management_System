package com.example.schoolmanagementsystem.backend.data.repository

import com.example.schoolmanagementsystem.backend.data.local.dao.TeacherDao
import com.example.schoolmanagementsystem.backend.data.local.entity.toDomain
import com.example.schoolmanagementsystem.backend.data.local.entity.toEntity
import com.example.schoolmanagementsystem.backend.data.manager.SessionManager
import com.example.schoolmanagementsystem.backend.data.remote.SikshaApiService
import com.example.schoolmanagementsystem.backend.domain.model.Teacher
import com.example.schoolmanagementsystem.backend.domain.repository.TeacherRepository
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TeacherRepositoryImpl @Inject constructor(
    private val apiService: SikshaApiService,
    private val sessionManager: SessionManager,
    private val teacherDao: TeacherDao
) : TeacherRepository {

    override fun getAllTeachers(): Flow<Resource<List<Teacher>>> = flow {
        // Emit local data immediately
        val localData = teacherDao.getAllTeachers().first()
        if (localData.isNotEmpty()) {
            emit(Resource.Success(localData.map { it.toDomain() }))
        } else {
            emit(Resource.Loading())
        }

        try {
            val response = apiService.getAllTeachers()
            if (response.success && response.data != null) {
                val remoteData = response.data
                remoteData.forEach { teacher ->
                    teacherDao.insertTeacher(teacher.toEntity())
                }
                emit(Resource.Success(remoteData))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: Exception) {
            if (localData.isEmpty()) {
                emit(Resource.Error(e.message ?: "An error occurred"))
            }
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getTeacherById(id: String): Resource<Teacher> = withContext(Dispatchers.IO) {
        try {
            // Try local first
            val localTeacher = teacherDao.getTeacherById(id)
            if (localTeacher != null) {
                return@withContext Resource.Success(localTeacher.toDomain())
            }

            val response = apiService.getTeacherById(id)
            if (response.success && response.data != null) {
                val teacher = response.data
                teacherDao.insertTeacher(teacher.toEntity())
                Resource.Success(teacher)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Teacher not found")
        }
    }

    override suspend fun addTeacher(teacher: Teacher): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.createTeacher(teacher)
            if (response.success && response.data != null) {
                teacherDao.insertTeacher(response.data.toEntity())
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add teacher")
        }
    }

    override suspend fun updateTeacher(teacher: Teacher): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.updateTeacher(teacher.id, teacher)
            if (response.success && response.data != null) {
                teacherDao.updateTeacher(response.data.toEntity())
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update teacher")
        }
    }

    override suspend fun deleteTeacher(teacher: Teacher): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.deleteTeacher(teacher.id)
            if (response.success) {
                teacherDao.deleteTeacher(teacher.toEntity())
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete teacher")
        }
    }
}
