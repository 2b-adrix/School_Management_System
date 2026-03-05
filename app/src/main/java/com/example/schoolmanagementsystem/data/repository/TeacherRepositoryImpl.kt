package com.example.schoolmanagementsystem.data.repository

import com.example.schoolmanagementsystem.domain.model.Teacher
import com.example.schoolmanagementsystem.domain.repository.TeacherRepository
import com.example.schoolmanagementsystem.domain.util.Resource
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TeacherRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest
) : TeacherRepository {

    override fun getAllTeachers(): Flow<Resource<List<Teacher>>> = flow {
        emit(Resource.Loading())
        try {
            val teachers = postgrest["teachers"]
                .select()
                .decodeList<Teacher>()
            emit(Resource.Success(teachers))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    override suspend fun getTeacherById(id: String): Resource<Teacher> {
        return try {
            val teacher = postgrest["teachers"]
                .select {
                    filter {
                        eq("id", id)
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
            postgrest["teachers"].insert(teacher)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add teacher")
        }
    }
}
