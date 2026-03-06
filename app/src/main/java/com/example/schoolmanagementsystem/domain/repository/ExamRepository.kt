package com.example.schoolmanagementsystem.domain.repository

import com.example.schoolmanagementsystem.domain.model.Exam
import com.example.schoolmanagementsystem.domain.model.Result
import com.example.schoolmanagementsystem.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface ExamRepository {
    fun getExamsByClass(classId: String): Flow<Resource<List<Exam>>>
    suspend fun addExam(exam: Exam): Resource<Unit>
    suspend fun updateExam(exam: Exam): Resource<Unit>
    suspend fun deleteExam(exam: Exam): Resource<Unit>
    fun getResultsByExam(examId: String): Flow<Resource<List<Result>>>
    suspend fun addResult(result: Result): Resource<Unit>
    fun getResultsByStudent(studentId: String): Flow<Resource<List<Result>>>
}
