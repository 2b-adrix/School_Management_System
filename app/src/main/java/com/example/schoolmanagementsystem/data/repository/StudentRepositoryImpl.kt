package com.example.schoolmanagementsystem.data.repository

import com.example.schoolmanagementsystem.data.manager.SessionManager
import com.example.schoolmanagementsystem.domain.model.Student
import com.example.schoolmanagementsystem.domain.repository.StudentRepository
import com.example.schoolmanagementsystem.domain.util.Resource
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class StudentRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val sessionManager: SessionManager
) : StudentRepository {

    override fun getAllStudents(): Flow<Resource<List<Student>>> = flow {
        emit(Resource.Loading())
        try {
            val schoolId = sessionManager.schoolId.firstOrNull()
            val students = postgrest["students"]
                .select {
                    filter {
                        eq("schoolId", schoolId ?: "")
                    }
                }
                .decodeList<Student>()
            emit(Resource.Success(students))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    override suspend fun getStudentById(id: String): Resource<Student> {
        return try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            val student = postgrest["students"]
                .select(columns = Columns.ALL) {
                    filter {
                        eq("id", id)
                        eq("schoolId", schoolId)
                    }
                }
                .decodeSingle<Student>()
            Resource.Success(student)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Student not found")
        }
    }

    override suspend fun addStudent(student: Student): Resource<Unit> {
        return try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            val studentWithSchoolId = student.copy(schoolId = schoolId)
            postgrest["students"].insert(studentWithSchoolId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add student")
        }
    }

    override suspend fun updateStudent(student: Student): Resource<Unit> {
        return try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            postgrest["students"].update(student.copy(schoolId = schoolId)) {
                filter {
                    eq("id", student.id)
                    eq("schoolId", schoolId)
                }
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update student")
        }
    }

    override suspend fun deleteStudent(student: Student): Resource<Unit> {
        return try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            postgrest["students"].delete {
                filter {
                    eq("id", student.id)
                    eq("schoolId", schoolId)
                }
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete student")
        }
    }
}
