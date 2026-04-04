package com.example.schoolmanagementsystem.frontend.ui.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolmanagementsystem.backend.domain.repository.AnnouncementRepository
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repository: AnnouncementRepository
) : ViewModel() {

    private val _state = MutableStateFlow(NotificationState())
    val state = _state.asStateFlow()

    init {
        loadAnnouncements()
    }

    private fun loadAnnouncements() {
        repository.getAllAnnouncements().onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
                }
                is Resource.Success -> {
                    val items = result.data?.map { 
                        AnnouncementItem(it.title, it.content, it.createdAt)
                    } ?: emptyList()
                    _state.value = _state.value.copy(announcements = items, isLoading = false)
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(isLoading = false, error = result.message)
                }
            }
        }.launchIn(viewModelScope)
    }

    data class NotificationState(
        val isLoading: Boolean = false,
        val announcements: List<AnnouncementItem> = emptyList(),
        val error: String? = null
    )

    data class AnnouncementItem(
        val title: String,
        val content: String,
        val time: String
    )
}

