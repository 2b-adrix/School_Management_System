package com.example.schoolmanagementsystem.backend.data.repository

import com.example.schoolmanagementsystem.backend.data.local.dao.SubjectDao
import com.example.schoolmanagementsystem.backend.data.local.entity.toDomain
import com.example.schoolmanagementsystem.backend.data.local.entity.toEntity
import com.example.schoolmanagementsystem.backend.data.manager.SessionManager
import com.example.schoolmanagementsystem.backend.data.remote.SikshaApiService
import com.example.schoolmanagementsystem.backend.domain.model.Subject
import com.example.schoolmanagementsystem.backend.domain.repository.SubjectRepository
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SubjectRepositoryImpl @Inject constructor(
    private val apiService: SikshaApiService,
    private val sessionManager: SessionManager,
    private val subjectDao: SubjectDao
) : SubjectRepository {

    override fun getAllSubjects(): Flow<Resource<List<Subject>>> = flow {
        // Emit local data immediately
        val localData = subjectDao.getAllSubjects().first()
        if (localData.isNotEmpty()) {
            emit(Resource.Success(localData.map { it.toDomain() }))
        } else {
            emit(Resource.Loading())
        }

        try {
            val response = apiService.getAllSubjects()
            if (response.success && response.data != null) {
                val remoteData = response.data
                remoteData.forEach { subject ->
                    subjectDao.insertSubject(subject.toEntity())
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

    override suspend fun getSubjectById(id: String): Resource<Subject> = withContext(Dispatchers.IO) {
        try {
            // Try local first
            val localData = subjectDao.getSubjectById(id)
            if (localData != null) {
                return@withContext Resource.Success(localData.toDomain())
            }

            val response = apiService.getAllSubjects() // Backend might need specific getById
            if (response.success && response.data != null) {
                val subject = response.data.find { it.id == id }
                if (subject != null) {
                    subjectDao.insertSubject(subject.toEntity())
                    Resource.Success(subject)
                } else {
                    Resource.Error("Subject not found")
                }
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Subject not found")
        }
    }

    override suspend fun addSubject(subject: Subject): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.addSubject(subject)
            if (response.success && response.data != null) {
                subjectDao.insertSubject(response.data.toEntity())
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add subject")
        }
    }

    override suspend fun updateSubject(subject: Subject): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            // Placeholder: Backend might need update endpoint
            subjectDao.updateSubject(subject.toEntity())
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update subject")
        }
    }

    override suspend fun deleteSubject(subject: Subject): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            // Placeholder: Backend might need delete endpoint
            subjectDao.deleteSubject(subject.toEntity())
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete subject")
        }
    }
}
