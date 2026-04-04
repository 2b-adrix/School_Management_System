package com.example.schoolmanagementsystem.backend.data.repository

import com.example.schoolmanagementsystem.backend.data.manager.SessionManager
import com.example.schoolmanagementsystem.backend.domain.model.Exam
import com.example.schoolmanagementsystem.backend.domain.model.Result
import com.example.schoolmanagementsystem.backend.domain.repository.ExamRepository
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ExamRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val sessionManager: SessionManager
) : ExamRepository {

    override fun getExamsByClass(classId: String): Flow<Resource<List<Exam>>> = flow {
        emit(Resource.Loading())
        try {
            val schoolId = sessionManager.schoolId.firstOrNull()
            val exams = postgrest["exams"]
                .select {
                    filter {
                        eq("school_id", schoolId ?: "")
                        eq("class_id", classId)
                    }
                }
                .decodeList<Exam>()
            emit(Resource.Success(exams))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun addExam(exam: Exam): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            val examWithSchoolId = exam.copy(schoolId = schoolId)
            postgrest["exams"].insert(examWithSchoolId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add exam")
        }
    }

    override suspend fun updateExam(exam: Exam): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            postgrest["exams"].update(exam.copy(schoolId = schoolId)) {
                filter {
                    eq("id", exam.id)
                    eq("school_id", schoolId)
                }
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update exam")
        }
    }

    override suspend fun deleteExam(exam: Exam): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            postgrest["exams"].delete {
                filter {
                    eq("id", exam.id)
                    eq("school_id", schoolId)
                }
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete exam")
        }
    }

    override fun getResultsByExam(examId: String): Flow<Resource<List<Result>>> = flow {
        emit(Resource.Loading())
        try {
            val schoolId = sessionManager.schoolId.firstOrNull()
            val results = postgrest["results"]
                .select {
                    filter {
                        eq("school_id", schoolId ?: "")
                        eq("exam_id", examId)
                    }
                }
                .decodeList<Result>()
            emit(Resource.Success(results))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun addResult(result: Result): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            val resultWithSchoolId = result.copy(schoolId = schoolId)
            postgrest["results"].insert(resultWithSchoolId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add result")
        }
    }

    override fun getResultsByStudent(studentId: String): Flow<Resource<List<Result>>> = flow {
        emit(Resource.Loading())
        try {
            val schoolId = sessionManager.schoolId.firstOrNull()
            val results = postgrest["results"]
                .select {
                    filter {
                        eq("school_id", schoolId ?: "")
                        eq("student_id", studentId)
                    }
                }
                .decodeList<Result>()
            emit(Resource.Success(results))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }.flowOn(Dispatchers.IO)
}

