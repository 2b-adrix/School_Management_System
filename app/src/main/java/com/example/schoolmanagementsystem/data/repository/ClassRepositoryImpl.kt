package com.example.schoolmanagementsystem.data.repository

import com.example.schoolmanagementsystem.data.manager.SessionManager
import com.example.schoolmanagementsystem.domain.model.SchoolClass
import com.example.schoolmanagementsystem.domain.repository.ClassRepository
import com.example.schoolmanagementsystem.domain.util.Resource
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ClassRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val sessionManager: SessionManager
) : ClassRepository {

    override fun getAllClasses(): Flow<Resource<List<SchoolClass>>> = flow {
        emit(Resource.Loading())
        try {
            val schoolId = sessionManager.schoolId.firstOrNull()
            val classes = postgrest["classes"]
                .select {
                    filter {
                        eq("school_id", schoolId ?: "")
                    }
                }
                .decodeList<SchoolClass>()
            emit(Resource.Success(classes))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    override suspend fun getClassById(id: String): Resource<SchoolClass> {
        return try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            val schoolClass = postgrest["classes"]
                .select {
                    filter {
                        eq("id", id)
                        eq("school_id", schoolId)
                    }
                }
                .decodeSingle<SchoolClass>()
            Resource.Success(schoolClass)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Class not found")
        }
    }

    override suspend fun addClass(schoolClass: SchoolClass): Resource<Unit> {
        return try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            val classWithSchoolId = schoolClass.copy(schoolId = schoolId)
            postgrest["classes"].insert(classWithSchoolId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add class")
        }
    }

    override suspend fun updateClass(schoolClass: SchoolClass): Resource<Unit> {
        return try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            postgrest["classes"].update(schoolClass.copy(schoolId = schoolId)) {
                filter {
                    eq("id", schoolClass.id)
                    eq("school_id", schoolId)
                }
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update class")
        }
    }

    override suspend fun deleteClass(schoolClass: SchoolClass): Resource<Unit> {
        return try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            postgrest["classes"].delete {
                filter {
                    eq("id", schoolClass.id)
                    eq("school_id", schoolId)
                }
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete class")
        }
    }
}
