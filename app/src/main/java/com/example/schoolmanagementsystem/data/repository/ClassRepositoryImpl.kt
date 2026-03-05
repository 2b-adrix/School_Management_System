package com.example.schoolmanagementsystem.data.repository

import com.example.schoolmanagementsystem.domain.model.SchoolClass
import com.example.schoolmanagementsystem.domain.repository.ClassRepository
import com.example.schoolmanagementsystem.domain.util.Resource
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ClassRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest
) : ClassRepository {

    override fun getAllClasses(): Flow<Resource<List<SchoolClass>>> = flow {
        emit(Resource.Loading())
        try {
            val classes = postgrest["classes"]
                .select()
                .decodeList<SchoolClass>()
            emit(Resource.Success(classes))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    override suspend fun getClassById(id: String): Resource<SchoolClass> {
        return try {
            val schoolClass = postgrest["classes"]
                .select {
                    filter {
                        eq("id", id)
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
            postgrest["classes"].insert(schoolClass)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add class")
        }
    }

    override suspend fun updateClass(schoolClass: SchoolClass): Resource<Unit> {
        return try {
            postgrest["classes"].update(schoolClass) {
                filter {
                    eq("id", schoolClass.id)
                }
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update class")
        }
    }

    override suspend fun deleteClass(schoolClass: SchoolClass): Resource<Unit> {
        return try {
            postgrest["classes"].delete {
                filter {
                    eq("id", schoolClass.id)
                }
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete class")
        }
    }
}
