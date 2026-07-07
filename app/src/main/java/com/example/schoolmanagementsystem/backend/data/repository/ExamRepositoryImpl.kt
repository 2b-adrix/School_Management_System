package com.example.schoolmanagementsystem.backend.data.repository

import com.example.schoolmanagementsystem.backend.data.local.dao.ExamDao
import com.example.schoolmanagementsystem.backend.data.local.dao.ResultDao
import com.example.schoolmanagementsystem.backend.data.local.entity.toDomain
import com.example.schoolmanagementsystem.backend.data.local.entity.toEntity
import com.example.schoolmanagementsystem.backend.data.manager.SessionManager
import com.example.schoolmanagementsystem.backend.data.remote.SikshaApiService
import com.example.schoolmanagementsystem.backend.domain.model.Exam
import com.example.schoolmanagementsystem.backend.domain.model.Result
import com.example.schoolmanagementsystem.backend.domain.repository.ExamRepository
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ExamRepositoryImpl @Inject constructor(
    private val apiService: SikshaApiService,
    private val sessionManager: SessionManager,
    private val examDao: ExamDao,
    private val resultDao: ResultDao
) : ExamRepository {

    override fun getExamsByClass(classId: String): Flow<Resource<List<Exam>>> = flow {
        // Emit local data immediately
        val localData = examDao.getExamsByClass(classId).first()
        if (localData.isNotEmpty()) {
            emit(Resource.Success(localData.map { it.toDomain() }))
        } else {
            emit(Resource.Loading())
        }

        try {
            val response = apiService.getAllExams()
            if (response.success && response.data != null) {
                val remoteData = response.data
                remoteData.forEach { exam ->
                    examDao.insertExam(exam.toEntity())
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

    override suspend fun addExam(exam: Exam): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.createExam(exam)
            if (response.success && response.data != null) {
                examDao.insertExam(response.data.toEntity())
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add exam")
        }
    }

    override suspend fun updateExam(exam: Exam): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.updateExam(exam.id, exam)
            if (response.success && response.data != null) {
                examDao.updateExam(response.data.toEntity())
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update exam")
        }
    }

    override suspend fun deleteExam(exam: Exam): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.deleteExam(exam.id)
            if (response.success) {
                examDao.deleteExam(exam.toEntity())
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete exam")
        }
    }

    override fun getResultsByExam(examId: String): Flow<Resource<List<Result>>> = flow {
        // Emit local data immediately
        val localData = resultDao.getResultsByExam(examId).first()
        if (localData.isNotEmpty()) {
            emit(Resource.Success(localData.map { it.toDomain() }))
        } else {
            emit(Resource.Loading())
        }

        try {
            // Using student results for now as placeholder
            val response = apiService.getResultsByStudent(examId) 
            if (response.success && response.data != null) {
                val remoteData = response.data
                remoteData.forEach { result ->
                    resultDao.insertResult(result.toEntity())
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

    override suspend fun addResult(result: Result): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.addResult(result)
            if (response.success && response.data != null) {
                resultDao.insertResult(response.data.toEntity())
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add result")
        }
    }

    override fun getResultsByStudent(studentId: String): Flow<Resource<List<Result>>> = flow {
        // Emit local data immediately
        val localData = resultDao.getResultsByStudent(studentId).first()
        if (localData.isNotEmpty()) {
            emit(Resource.Success(localData.map { it.toDomain() }))
        } else {
            emit(Resource.Loading())
        }

        try {
            val response = apiService.getResultsByStudent(studentId)
            if (response.success && response.data != null) {
                val remoteData = response.data
                remoteData.forEach { result ->
                    resultDao.insertResult(result.toEntity())
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
}
