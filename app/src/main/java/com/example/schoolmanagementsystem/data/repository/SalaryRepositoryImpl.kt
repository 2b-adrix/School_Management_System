package com.example.schoolmanagementsystem.data.repository

import com.example.schoolmanagementsystem.data.manager.SessionManager
import com.example.schoolmanagementsystem.domain.model.SalaryRecord
import com.example.schoolmanagementsystem.domain.repository.SalaryRepository
import com.example.schoolmanagementsystem.domain.util.Resource
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SalaryRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val sessionManager: SessionManager
) : SalaryRepository {

    override fun getAllSalaries(): Flow<Resource<List<SalaryRecord>>> = flow {
        emit(Resource.Loading())
        try {
            val schoolId = sessionManager.schoolId.firstOrNull()
            val salaries = postgrest["salaries"]
                .select {
                    filter {
                        eq("school_id", schoolId ?: "")
                    }
                }
                .decodeList<SalaryRecord>()
            emit(Resource.Success(salaries))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    override fun getSalariesByTeacher(teacherId: String): Flow<Resource<List<SalaryRecord>>> = flow {
        emit(Resource.Loading())
        try {
            val schoolId = sessionManager.schoolId.firstOrNull()
            val salaries = postgrest["salaries"]
                .select {
                    filter {
                        eq("school_id", schoolId ?: "")
                        eq("teacher_id", teacherId)
                    }
                }
                .decodeList<SalaryRecord>()
            emit(Resource.Success(salaries))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    override suspend fun addSalaryRecord(salaryRecord: SalaryRecord): Resource<Unit> {
        return try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            postgrest["salaries"].insert(salaryRecord.copy(schoolId = schoolId))
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add salary record")
        }
    }

    override suspend fun updateSalaryStatus(salaryId: String, status: String): Resource<Unit> {
        return try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            postgrest["salaries"].update({
                set("status", status)
            }) {
                filter {
                    eq("id", salaryId)
                    eq("school_id", schoolId)
                }
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update salary status")
        }
    }

    override suspend fun deleteSalaryRecord(salaryId: String): Resource<Unit> {
        return try {
            val schoolId = sessionManager.schoolId.firstOrNull() ?: ""
            postgrest["salaries"].delete {
                filter {
                    eq("id", salaryId)
                    eq("school_id", schoolId)
                }
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete salary record")
        }
    }
}
