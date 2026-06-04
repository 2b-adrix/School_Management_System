package com.example.schoolmanagementsystem.backend.data.repository

import com.example.schoolmanagementsystem.backend.data.local.dao.StudentDao
import com.example.schoolmanagementsystem.backend.data.local.entity.toDomain
import com.example.schoolmanagementsystem.backend.data.local.entity.toEntity
import com.example.schoolmanagementsystem.backend.data.manager.SessionManager
import com.example.schoolmanagementsystem.backend.data.remote.SikshaApiService
import com.example.schoolmanagementsystem.backend.domain.model.Student
import com.example.schoolmanagementsystem.backend.domain.repository.StudentRepository
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject

class StudentRepositoryImpl @Inject constructor(
    private val apiService: SikshaApiService,
    private val sessionManager: SessionManager,
    private val studentDao: StudentDao
) : StudentRepository {

    override fun getAllStudents(): Flow<Resource<List<Student>>> = flow {
        // Emit local data immediately on the IO dispatcher
        val localStudents = studentDao.getAllStudents().first()
        if (localStudents.isNotEmpty()) {
            emit(Resource.Success(localStudents.map { it.toDomain() }))
        } else {
            emit(Resource.Loading())
        }

        try {
            val response = apiService.getAllStudents()
            if (response.success && response.data != null) {
                val remoteStudents = response.data
                // Sync local DB
                remoteStudents.forEach { student ->
                    studentDao.insertStudent(student.toEntity())
                }
                emit(Resource.Success(remoteStudents))
            } else {
                emit(Resource.Error(response.message))
            }
        } catch (e: Exception) {
            if (localStudents.isEmpty()) {
                emit(Resource.Error(e.message ?: "An error occurred"))
            }
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getStudentById(id: String): Resource<Student> = withContext(Dispatchers.IO) {
        try {
            // Try local first
            val localStudent = studentDao.getStudentById(id)
            if (localStudent != null) {
                return@withContext Resource.Success(localStudent.toDomain())
            }

            val response = apiService.getStudentById(id)
            if (response.success && response.data != null) {
                val student = response.data
                // Cache locally
                studentDao.insertStudent(student.toEntity())
                Resource.Success(student)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Student not found")
        }
    }

    override suspend fun addStudent(student: Student): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.addStudent(student)
            if (response.success && response.data != null) {
                val savedStudent = response.data
                studentDao.insertStudent(savedStudent.toEntity())
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add student")
        }
    }

    override suspend fun bulkAddStudents(students: List<Student>): Resource<Unit> = withContext(Dispatchers.IO) {
        // For simplicity, we could loop or add a bulk endpoint to the backend
        // For now, let's just do it one by one or leave it for later
        try {
            students.forEach { addStudent(it) }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Bulk add failed")
        }
    }

    override suspend fun updateStudent(student: Student): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.updateStudent(student.id, student)
            if (response.success && response.data != null) {
                val updatedStudent = response.data
                studentDao.updateStudent(updatedStudent.toEntity())
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update student")
        }
    }

    override suspend fun deleteStudent(student: Student): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.deleteStudent(student.id)
            if (response.success) {
                studentDao.deleteStudent(student.toEntity())
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete student")
        }
    }
}

