package com.example.schoolmanagementsystem.domain.repository

import com.example.schoolmanagementsystem.domain.model.SalaryRecord
import com.example.schoolmanagementsystem.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface SalaryRepository {
    fun getAllSalaries(): Flow<Resource<List<SalaryRecord>>>
    fun getSalariesByTeacher(teacherId: String): Flow<Resource<List<SalaryRecord>>>
    suspend fun addSalaryRecord(salaryRecord: SalaryRecord): Resource<Unit>
    suspend fun updateSalaryStatus(salaryId: String, status: String): Resource<Unit>
    suspend fun deleteSalaryRecord(salaryId: String): Resource<Unit>
}
