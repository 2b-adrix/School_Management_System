package com.example.schoolmanagementsystem.ui.attendance

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolmanagementsystem.domain.model.*
import com.example.schoolmanagementsystem.domain.repository.*
import com.example.schoolmanagementsystem.domain.service.AttendanceAIService
import com.example.schoolmanagementsystem.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val attendanceRepository: AttendanceRepository,
    private val studentRepository: StudentRepository,
    private val subjectRepository: SubjectRepository,
    private val classRepository: ClassRepository,
    private val authRepository: AuthRepository,
    private val aiService: AttendanceAIService
) : ViewModel() {
    private val _state = MutableStateFlow(AttendanceState())
    val state = _state.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        observeUser()
        loadClasses()
        loadSubjects()
    }

    private fun observeUser() {
        authRepository.getCurrentUser()
            .onEach { user -> _state.value = _state.value.copy(user = user) }
            .launchIn(viewModelScope)
    }

    private fun loadClasses() {
        viewModelScope.launch {
            classRepository.getAllClasses().collect {
                _state.value = _state.value.copy(classes = it)
            }
        }
    }

    private fun loadSubjects() {
        viewModelScope.launch {
            subjectRepository.getAllSubjects().collect {
                _state.value = _state.value.copy(subjects = it)
            }
        }
    }

    fun selectClass(classId: String) {
        _state.value = _state.value.copy(selectedClassId = classId)
        loadStudents(classId)
    }

    private fun loadStudents(classId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            studentRepository.getAllStudents().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val students = result.data?.filter { it.classId == classId } ?: emptyList()
                        val attendanceRecords = students.associate { student ->
                            student.id to AttendanceRecord(
                                id = UUID.randomUUID().toString(),
                                schoolId = _state.value.user?.schoolId ?: "",
                                studentId = student.id,
                                classId = classId,
                                subjectId = _state.value.selectedSubjectId,
                                date = _state.value.selectedDate.toString(),
                                isPresent = false
                            )
                        }
                        _state.value = _state.value.copy(
                            students = students,
                            attendanceRecords = attendanceRecords,
                            isLoading = false
                        )
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(students = emptyList(), isLoading = false)
                    }
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isLoading = true)
                    }
                }
            }
        }
    }

    // --- AI Integration Methods ---

    fun onFaceCaptured(bitmap: Bitmap) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isAiProcessing = true)
            when (val result = aiService.recognizeStudentsFromImage(bitmap)) {
                is Resource.Success -> {
                    result.data?.forEach { studentId ->
                        onAttendanceChanged(studentId, true)
                    }
                    _eventFlow.emit(UiEvent.ShowSnackbar("AI identified ${result.data?.size} students"))
                }
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.ShowSnackbar(result.message ?: "AI Recognition failed"))
                }
                else -> Unit
            }
            _state.value = _state.value.copy(isAiProcessing = false)
        }
    }

    fun onQrScanned(bitmap: Bitmap) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isAiProcessing = true)
            when (val result = aiService.scanAttendanceQR(bitmap)) {
                is Resource.Success -> {
                    val studentId = result.data ?: ""
                    onAttendanceChanged(studentId, true)
                    _eventFlow.emit(UiEvent.ShowSnackbar("QR Check-in successful"))
                }
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.ShowSnackbar(result.message ?: "Invalid QR Code"))
                }
                else -> Unit
            }
            _state.value = _state.value.copy(isAiProcessing = false)
        }
    }

    fun onVoiceRecorded(audioFile: File) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isAiProcessing = true)
            val studentNames = _state.value.students.map { "${it.firstName} ${it.lastName}" }
            when (val result = aiService.processVoiceAttendance(audioFile, studentNames)) {
                is Resource.Success -> {
                    result.data?.forEach { studentId ->
                        onAttendanceChanged(studentId, true)
                    }
                    _eventFlow.emit(UiEvent.ShowSnackbar("Voice roll call processed"))
                }
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.ShowSnackbar(result.message ?: "Voice processing failed"))
                }
                else -> Unit
            }
            _state.value = _state.value.copy(isAiProcessing = false)
        }
    }

    fun onAttendanceChanged(studentId: String, isPresent: Boolean) {
        val currentRecords = _state.value.attendanceRecords.toMutableMap()
        currentRecords[studentId]?.let {
            currentRecords[studentId] = it.copy(isPresent = isPresent)
            _state.value = _state.value.copy(attendanceRecords = currentRecords)
        }
    }

    fun saveAttendance() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true)
            val records = _state.value.attendanceRecords.values.toList()
            when (val result = attendanceRepository.saveAttendance(records)) {
                is Resource.Success -> {
                    _eventFlow.emit(UiEvent.SaveSuccess)
                }
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.ShowSnackbar(result.message ?: "Failed to save attendance"))
                }
                else -> Unit
            }
            _state.value = _state.value.copy(isSaving = false)
        }
    }

    data class AttendanceState(
        val user: User? = null,
        val classes: Resource<List<SchoolClass>> = Resource.Loading(),
        val subjects: Resource<List<Subject>> = Resource.Loading(),
        val students: List<Student> = emptyList(),
        val attendanceRecords: Map<String, AttendanceRecord> = emptyMap(),
        val selectedClassId: String = "",
        val selectedSubjectId: String = "",
        val selectedDate: Date = Date(),
        val isLoading: Boolean = false,
        val isSaving: Boolean = false,
        val isAiProcessing: Boolean = false,
        val aiMode: AiMode = AiMode.NONE
    )

    enum class AiMode { NONE, FACE, QR, VOICE }

    sealed class UiEvent {
        object SaveSuccess : UiEvent()
        data class ShowSnackbar(val message: String) : UiEvent()
    }
}
