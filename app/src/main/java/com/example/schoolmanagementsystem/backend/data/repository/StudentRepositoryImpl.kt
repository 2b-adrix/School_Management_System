package com.example.schoolmanagementsystem.backend.data.repository

import com.example.schoolmanagementsystem.backend.data.local.dao.StudentDao
import com.example.schoolmanagementsystem.backend.data.local.entity.toDomain
import com.example.schoolmanagementsystem.backend.data.local.entity.toEntity
import com.example.schoolmanagementsystem.backend.data.manager.SessionManager
import com.example.schoolmanagementsystem.backend.domain.model.Student
import com.example.schoolmanagementsystem.backend.domain.repository.StudentRepository
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject

class StudentRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
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
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            val remoteStudents = postgrest["students"]
                .select {
                    filter {
                        eq("school_id", schoolId)
                    }
                }
                .decodeList<Student>()
            
            // Sync local DB in a single transaction if possible, 
            // but here we'll just do it safely on IO
            remoteStudents.forEach { student ->
                studentDao.insertStudent(student.toEntity())
            }
            
            emit(Resource.Success(remoteStudents))
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

            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            val student = postgrest["students"]
                .select(columns = Columns.ALL) {
                    filter {
                        eq("id", id)
                        eq("school_id", schoolId)
                    }
                }
                .decodeSingle<Student>()
            
            // Cache locally
            studentDao.insertStudent(student.toEntity())
            Resource.Success(student)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Student not found")
        }
    }

    override suspend fun addStudent(student: Student): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            val studentWithId = student.copy(schoolId = schoolId)
            
            // Save locally first
            studentDao.insertStudent(studentWithId.toEntity())
            
            // Sync to remote
            postgrest["students"].insert(studentWithId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            // Even if remote fails, it's in local DB
            Resource.Success(Unit)
        }
    }

    override suspend fun bulkAddStudents(students: List<Student>): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            val studentsWithId = students.map { it.copy(schoolId = schoolId) }
            
            // Bulk insert locally
            studentsWithId.forEach { studentDao.insertStudent(it.toEntity()) }
            
            postgrest["students"].insert(studentsWithId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Success(Unit)
        }
    }

    override suspend fun updateStudent(student: Student): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            val updatedStudent = student.copy(schoolId = schoolId)
            
            // Update local
            studentDao.updateStudent(updatedStudent.toEntity())
            
            postgrest["students"].update(updatedStudent) {
                filter {
                    eq("id", student.id)
                    eq("school_id", schoolId)
                }
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update student")
        }
    }

    override suspend fun deleteStudent(student: Student): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            
            // Delete local
            studentDao.deleteStudent(student.toEntity())

            postgrest["students"].delete {
                filter {
                    eq("id", student.id)
                    eq("school_id", schoolId)
                }
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete student")
        }
    }
}

