package com.example.schoolmanagementsystem.backend.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.schoolmanagementsystem.backend.data.local.dao.AttendanceDao
import com.example.schoolmanagementsystem.backend.data.local.entity.toDomain
import io.github.jan.supabase.postgrest.Postgrest
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class AttendanceSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val attendanceDao: AttendanceDao,
    private val postgrest: Postgrest
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val unsynced = attendanceDao.getUnsyncedAttendance()
            if (unsynced.isEmpty()) return Result.success()

            val records = unsynced.map { it.toDomain() }
            postgrest["attendance"].upsert(records)
            
            attendanceDao.markAsSynced(unsynced.map { it.id })
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }
}

