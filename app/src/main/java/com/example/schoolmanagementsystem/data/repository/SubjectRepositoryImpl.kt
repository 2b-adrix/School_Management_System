package com.example.schoolmanagementsystem.data.repository

import com.example.schoolmanagementsystem.domain.model.Subject
import com.example.schoolmanagementsystem.domain.repository.SubjectRepository
import com.example.schoolmanagementsystem.domain.util.Resource
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SubjectRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest
) : SubjectRepository {
    override fun getAllSubjects(): Flow<Resource<List<Subject>>> = flow {
        emit(Resource.Loading())
        try {
            val subjects = postgrest["subjects"]
                .select()
                .decodeList<Subject>()
            emit(Resource.Success(subjects))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    override suspend fun getSubjectById(id: String): Resource<Subject> {
        return try {
            val subject = postgrest["subjects"]
                .select {
                    filter {
                        eq("id", id)
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
            postgrest["subjects"].insert(subject)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add subject")
        }
    }
}
