package com.example.schoolmanagementsystem.data.repository

import com.example.schoolmanagementsystem.data.manager.SessionManager
import com.example.schoolmanagementsystem.domain.model.Subject
import com.example.schoolmanagementsystem.domain.repository.SubjectRepository
import com.example.schoolmanagementsystem.domain.util.Resource
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
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
                        eq("schoolId", schoolId ?: "")
                    }
                }
                .decodeList<Subject>()
            emit(Resource.Success(subjects))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    override suspend fun getSubjectById(id: String): Resource<Subject> {
        return try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            val subject = postgrest["subjects"]
                .select {
                    filter {
                        eq("id", id)
                        eq("schoolId", schoolId)
                    }
                }
                .decodeSingle<Subject>()
            Resource.Success(subject)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Subject not found")
        }
    }

    override suspend fun addSubject(subject: Subject): Resource<Unit> {
        return try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            val subjectWithSchoolId = subject.copy(schoolId = schoolId)
            postgrest["subjects"].insert(subjectWithSchoolId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add subject")
        }
    }
}
