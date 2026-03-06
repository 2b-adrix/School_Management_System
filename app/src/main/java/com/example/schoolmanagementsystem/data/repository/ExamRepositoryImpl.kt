package com.example.schoolmanagementsystem.data.repository

import com.example.schoolmanagementsystem.domain.model.Exam
import com.example.schoolmanagementsystem.domain.model.Result
import com.example.schoolmanagementsystem.domain.repository.ExamRepository
import com.example.schoolmanagementsystem.domain.util.Resource
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ExamRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest
) : ExamRepository {

    override fun getExamsByClass(classId: String): Flow<Resource<List<Exam>>> = flow {
        emit(Resource.Loading())
        try {
            val exams = postgrest["exams"]
                .select {
                    filter {
                        eq("classId", classId)
                    }
                }
                .decodeList<Exam>()
            emit(Resource.Success(exams))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    override suspend fun addExam(exam: Exam): Resource<Unit> {
        return try {
            postgrest["exams"].insert(exam)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add exam")
        }
    }

    override suspend fun updateExam(exam: Exam): Resource<Unit> {
        return try {
            postgrest["exams"].update(exam) {
                filter {
                    eq("id", exam.id)
                }
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update exam")
        }
    }

    override suspend fun deleteExam(exam: Exam): Resource<Unit> {
        return try {
            postgrest["exams"].delete {
                filter {
                    eq("id", exam.id)
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
            val results = postgrest["results"]
                .select {
                    filter {
                        eq("examId", examId)
                    }
                }
                .decodeList<Result>()
            emit(Resource.Success(results))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    override suspend fun addResult(result: Result): Resource<Unit> {
        return try {
            postgrest["results"].insert(result)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add result")
        }
    }

    override fun getResultsByStudent(studentId: String): Flow<Resource<List<Result>>> = flow {
        emit(Resource.Loading())
        try {
            val results = postgrest["results"]
                .select {
                    filter {
                        eq("studentId", studentId)
                    }
                }
                .decodeList<Result>()
            emit(Resource.Success(results))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }
}
