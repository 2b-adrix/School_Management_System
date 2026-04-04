package com.example.schoolmanagementsystem.frontend.ui.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolmanagementsystem.backend.domain.model.Announcement
import com.example.schoolmanagementsystem.backend.domain.repository.AnnouncementRepository
import com.example.schoolmanagementsystem.backend.domain.repository.AuthRepository
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddAnnouncementViewModel @Inject constructor(
    private val repository: AnnouncementRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _saveState = MutableStateFlow<Resource<Unit>?>(null)
    val saveState = _saveState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun saveAnnouncement(title: String, content: String, targetRole: String, targetClassId: String?) {
        if (title.isBlank() || content.isBlank()) {
            viewModelScope.launch {
                _eventFlow.emit(UiEvent.ShowSnackbar("Title and content are required"))
            }
            return
        }

        viewModelScope.launch {
            _saveState.value = Resource.Loading()
            val user = authRepository.getCurrentUser().firstOrNull()
            
            val announcement = Announcement(
                id = UUID.randomUUID().toString(),
                schoolId = user?.schoolId ?: "",
                title = title,
                content = content,
                targetRole = targetRole,
                targetId = targetClassId,
                createdAt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
            )

            val result = repository.addAnnouncement(announcement)
            _saveState.value = result
            if (result is Resource.Success) {
                _eventFlow.emit(UiEvent.SaveSuccess)
            } else if (result is Resource.Error) {
                _eventFlow.emit(UiEvent.ShowSnackbar(result.message ?: "Failed to save"))
            }
        }
    }

    sealed class UiEvent {
        object SaveSuccess : UiEvent()
        data class ShowSnackbar(val message: String) : UiEvent()
    }
}

