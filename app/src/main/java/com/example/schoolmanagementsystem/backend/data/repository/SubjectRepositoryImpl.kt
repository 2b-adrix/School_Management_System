package com.example.schoolmanagementsystem.backend.data.repository

import com.example.schoolmanagementsystem.backend.data.manager.SessionManager
import com.example.schoolmanagementsystem.backend.domain.model.Subject
import com.example.schoolmanagementsystem.backend.domain.repository.SubjectRepository
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SubjectRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val sessionManager: SessionManager
) : SubjectRepository {
    override fun getAllSubjects(): Flow<Resource<List<Subject>>> = flow {
        emit(Resource.Loading())
        try {
            val schoolId = sessionManager.schoolId.firstOrNull()
            val subjects = postgrest["subjects"]
                .select {
                    filter {
                        eq("school_id", schoolId ?: "")
                    }
                }
                .decodeList<Subject>()
            emit(Resource.Success(subjects))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getSubjectById(id: String): Resource<Subject> = withContext(Dispatchers.IO) {
        try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            val subject = postgrest["subjects"]
                .select {
                    filter {
                        eq("id", id)
                        eq("school_id", schoolId)
                    }
                }
                .decodeSingle<Subject>()
            Resource.Success(subject)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Subject not found")
        }
    }

    override suspend fun addSubject(subject: Subject): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            val subjectWithSchoolId = subject.copy(schoolId = schoolId)
            postgrest["subjects"].insert(subjectWithSchoolId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add subject")
        }
    }

    override suspend fun updateSubject(subject: Subject): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            postgrest["subjects"].update(subject.copy(schoolId = schoolId)) {
                filter {
                    eq("id", subject.id)
                    eq("school_id", schoolId)
                }
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update subject")
        }
    }

    override suspend fun deleteSubject(subject: Subject): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            postgrest["subjects"].delete {
                filter {
                    eq("id", subject.id)
                    eq("school_id", schoolId)
                }
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete subject")
        }
    }
}

