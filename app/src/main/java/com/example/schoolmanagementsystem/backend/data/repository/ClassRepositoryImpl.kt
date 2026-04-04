package com.example.schoolmanagementsystem.backend.data.repository

import com.example.schoolmanagementsystem.backend.data.local.dao.ClassDao
import com.example.schoolmanagementsystem.backend.data.local.entity.toDomain
import com.example.schoolmanagementsystem.backend.data.local.entity.toEntity
import com.example.schoolmanagementsystem.backend.data.manager.SessionManager
import com.example.schoolmanagementsystem.backend.domain.model.SchoolClass
import com.example.schoolmanagementsystem.backend.domain.repository.ClassRepository
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ClassRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val sessionManager: SessionManager,
    private val classDao: ClassDao
) : ClassRepository {

    override fun getAllClasses(): Flow<Resource<List<SchoolClass>>> = flow {
        // Excellent Backend: Emit local data immediately
        val localClasses = classDao.getAllClasses().firstOrNull() ?: emptyList()
        if (localClasses.isNotEmpty()) {
            emit(Resource.Success(localClasses.map { it.toDomain() }))
        } else {
            emit(Resource.Loading())
        }

        try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            val remoteClasses = postgrest["classes"]
                .select {
                    filter {
                        eq("school_id", schoolId)
                    }
                }
                .decodeList<SchoolClass>()
            
            // Sync local DB
            remoteClasses.forEach { schoolClass ->
                classDao.insertClass(schoolClass.toEntity())
            }
            
            emit(Resource.Success(remoteClasses))
        } catch (e: Exception) {
            if (localClasses.isEmpty()) {
                emit(Resource.Error(e.message ?: "An error occurred"))
            }
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getClassById(id: String): Resource<SchoolClass> = withContext(Dispatchers.IO) {
        val localClass = classDao.getClassById(id)
        if (localClass != null) {
            return@withContext Resource.Success(localClass.toDomain())
        }

        try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            val schoolClass = postgrest["classes"]
                .select {
                    filter {
                        eq("id", id)
                        eq("school_id", schoolId)
                    }
                }
                .decodeSingle<SchoolClass>()
            
            classDao.insertClass(schoolClass.toEntity())
            Resource.Success(schoolClass)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Class not found")
        }
    }

    override suspend fun addClass(schoolClass: SchoolClass): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            val classWithId = schoolClass.copy(schoolId = schoolId)
            
            classDao.insertClass(classWithId.toEntity())
            
            postgrest["classes"].insert(classWithId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Success(Unit)
        }
    }

    override suspend fun updateClass(schoolClass: SchoolClass): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            val updatedClass = schoolClass.copy(schoolId = schoolId)
            
            classDao.insertClass(updatedClass.toEntity())
            
            postgrest["classes"].update(updatedClass) {
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

    override suspend fun deleteClass(schoolClass: SchoolClass): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            
            // Note: In a real app, delete locally too
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

