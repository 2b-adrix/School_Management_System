package com.example.schoolmanagementsystem.data.repository

import com.example.schoolmanagementsystem.data.manager.SessionManager
import com.example.schoolmanagementsystem.domain.model.Teacher
import com.example.schoolmanagementsystem.domain.repository.TeacherRepository
import com.example.schoolmanagementsystem.domain.util.Resource
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TeacherRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val sessionManager: SessionManager
) : TeacherRepository {

    override fun getAllTeachers(): Flow<Resource<List<Teacher>>> = flow {
        emit(Resource.Loading())
        try {
            val schoolId = sessionManager.schoolId.firstOrNull()
            val teachers = postgrest["teachers"]
                .select {
                    filter {
                        eq("school_id", schoolId ?: "")
                    }
                }
                .decodeList<Teacher>()
            emit(Resource.Success(teachers))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    override suspend fun getTeacherById(id: String): Resource<Teacher> {
        return try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            val teacher = postgrest["teachers"]
                .select {
                    filter {
                        eq("id", id)
                        eq("school_id", schoolId)
                    }
                }
                .decodeSingle<Teacher>()
            Resource.Success(teacher)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Teacher not found")
        }
    }

    override suspend fun addTeacher(teacher: Teacher): Resource<Unit> {
        return try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            val teacherWithSchoolId = teacher.copy(schoolId = schoolId)
            postgrest["teachers"].insert(teacherWithSchoolId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add teacher")
        }
    }

    override suspend fun updateTeacher(teacher: Teacher): Resource<Unit> {
        return try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            postgrest["teachers"].update(teacher.copy(schoolId = schoolId)) {
                filter {
                    eq("id", teacher.id)
                    eq("school_id", schoolId)
                }
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update teacher")
        }
    }

    override suspend fun deleteTeacher(teacher: Teacher): Resource<Unit> {
        return try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            postgrest["teachers"].delete {
                filter {
                    eq("id", teacher.id)
                    eq("school_id", schoolId)
                }
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete teacher")
        }
    }
}
