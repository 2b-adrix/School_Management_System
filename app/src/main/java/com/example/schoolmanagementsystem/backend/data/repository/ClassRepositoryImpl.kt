package com.example.schoolmanagementsystem.backend.data.repository

import com.example.schoolmanagementsystem.backend.data.local.dao.ClassDao
import com.example.schoolmanagementsystem.backend.data.local.entity.toDomain
import com.example.schoolmanagementsystem.backend.data.local.entity.toEntity
import com.example.schoolmanagementsystem.backend.data.manager.SessionManager
import com.example.schoolmanagementsystem.backend.data.remote.SikshaApiService
import com.example.schoolmanagementsystem.backend.domain.model.SchoolClass
import com.example.schoolmanagementsystem.backend.domain.repository.ClassRepository
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ClassRepositoryImpl @Inject constructor(
    private val apiService: SikshaApiService,
    private val sessionManager: SessionManager,
    private val classDao: ClassDao
) : ClassRepository {

    override fun getAllClasses(): Flow<Resource<List<SchoolClass>>> = flow {
        // Emit local data immediately on the IO dispatcher
        val localClasses = classDao.getAllClasses().first()
        if (localClasses.isNotEmpty()) {
            emit(Resource.Success(localClasses.map { it.toDomain() }))
        } else {
            emit(Resource.Loading())
        }

        try {
            val response = apiService.getAllClasses()
            if (response.success && response.data != null) {
                val remoteClasses = response.data
                remoteClasses.forEach { schoolClass ->
                    classDao.insertClass(schoolClass.toEntity())
                }
                emit(Resource.Success(remoteClasses))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: Exception) {
            if (localClasses.isEmpty()) {
                emit(Resource.Error(e.message ?: "An error occurred"))
            }
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getClassById(id: String): Resource<SchoolClass> = withContext(Dispatchers.IO) {
        try {
            // Try local first
            val localClass = classDao.getClassById(id)
            if (localClass != null) {
                return@withContext Resource.Success(localClass.toDomain())
            }

            val response = apiService.getClassById(id)
            if (response.success && response.data != null) {
                val schoolClass = response.data
                classDao.insertClass(schoolClass.toEntity())
                Resource.Success(schoolClass)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Class not found")
        }
    }

    override suspend fun addClass(schoolClass: SchoolClass): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.createClass(schoolClass)
            if (response.success && response.data != null) {
                classDao.insertClass(response.data.toEntity())
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add class")
        }
    }

    override suspend fun updateClass(schoolClass: SchoolClass): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.updateClass(schoolClass.id, schoolClass)
            if (response.success && response.data != null) {
                classDao.updateClass(response.data.toEntity())
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update class")
        }
    }

    override suspend fun deleteClass(schoolClass: SchoolClass): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.deleteClass(schoolClass.id)
            if (response.success) {
                classDao.deleteClass(schoolClass.toEntity())
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete class")
        }
    }
}
