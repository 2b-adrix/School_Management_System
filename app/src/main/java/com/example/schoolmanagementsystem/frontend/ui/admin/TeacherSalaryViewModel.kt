package com.example.schoolmanagementsystem.frontend.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolmanagementsystem.backend.domain.model.SalaryRecord
import com.example.schoolmanagementsystem.backend.domain.model.Teacher
import com.example.schoolmanagementsystem.backend.domain.repository.SalaryRepository
import com.example.schoolmanagementsystem.backend.domain.repository.TeacherRepository
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TeacherSalaryViewModel @Inject constructor(
    private val salaryRepository: SalaryRepository,
    private val teacherRepository: TeacherRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SalaryState())
    val state = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            combine(
                salaryRepository.getAllSalaries(),
                teacherRepository.getAllTeachers()
            ) { salariesRes, teachersRes ->
                if (salariesRes is Resource.Success && teachersRes is Resource.Success) {
                    _state.update { 
                        it.copy(
                            isLoading = false,
                            salaries = salariesRes.data ?: emptyList(),
                            teachers = teachersRes.data ?: emptyList()
                        )
                    }
                } else if (salariesRes is Resource.Error || teachersRes is Resource.Error) {
                    _state.update { it.copy(isLoading = false, error = "Failed to load data") }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun addSalaryRecord(teacherId: String, teacherName: String, amount: Double, month: String) {
        viewModelScope.launch {
            val record = SalaryRecord(
                id = UUID.randomUUID().toString(),
                schoolId = "", // Handled by repository
                teacherId = teacherId,
                teacherName = teacherName,
                amount = amount,
                month = month,
                paymentDate = "", // Set when paid
                status = "PENDING"
            )
            val result = salaryRepository.addSalaryRecord(record)
            if (result is Resource.Success) {
                _eventFlow.emit(UiEvent.ShowSnackbar("Salary record added"))
                loadData()
            } else {
                _eventFlow.emit(UiEvent.ShowSnackbar(result.message ?: "Failed to add record"))
            }
        }
    }

    fun updateStatus(salaryId: String, status: String) {
        viewModelScope.launch {
            val result = salaryRepository.updateSalaryStatus(salaryId, status)
            if (result is Resource.Success) {
                _eventFlow.emit(UiEvent.ShowSnackbar("Status updated to $status"))
                loadData()
            }
        }
    }

    data class SalaryState(
        val isLoading: Boolean = false,
        val salaries: List<SalaryRecord> = emptyList(),
        val teachers: List<Teacher> = emptyList(),
        val error: String? = null
    )

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
    }
}

