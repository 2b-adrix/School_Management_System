package com.example.schoolmanagementsystem.data.remote

import com.example.schoolmanagementsystem.domain.model.Student
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.Serializable
import javax.inject.Inject

@Serializable
data class StudentRemote(
    val id: String,
    val first_name: String,
    val last_name: String,
    val roll_number: String,
    val class_name: String,
    val parent_name: String,
    val parent_contact: String,
    val address: String,
    val date_of_birth: String
)

class SupabaseStudentDataSource @Inject constructor(
    private val postgrest: Postgrest
) {
    suspend fun getAllStudents(): List<StudentRemote> {
        return postgrest["students"].select().decodeList<StudentRemote>()
    }

    suspend fun insertStudent(student: StudentRemote) {
        postgrest["students"].insert(student)
    }
}
