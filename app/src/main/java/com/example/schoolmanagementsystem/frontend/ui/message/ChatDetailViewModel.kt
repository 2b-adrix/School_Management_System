package com.example.schoolmanagementsystem.frontend.ui.message

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolmanagementsystem.backend.domain.model.ChatMessage
import com.example.schoolmanagementsystem.backend.domain.repository.AuthRepository
import com.example.schoolmanagementsystem.backend.domain.repository.MessageRepository
import com.example.schoolmanagementsystem.backend.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ChatDetailViewModel @Inject constructor(
    private val repository: MessageRepository,
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val receiverId: String = savedStateHandle.get<String>("receiverId") ?: ""
    private val receiverName: String = savedStateHandle.get<String>("receiverName") ?: ""

    private val _state = MutableStateFlow(ChatDetailState(receiverName = receiverName))
    val state = _state.asStateFlow()

    init {
        loadMessages()
    }

    private fun loadMessages() {
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser().firstOrNull() ?: return@launch
            _state.update { it.copy(currentUserId = currentUser.id) }

            repository.getChatMessages(currentUser.id, receiverId).onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        val messages = result.data?.sortedBy { it.createdAt } ?: emptyList()
                        _state.update { it.copy(messages = messages) }
                    }
                    else -> {}
                }
            }.launchIn(viewModelScope)
        }
    }

    fun sendMessage(content: String) {
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser().firstOrNull() ?: return@launch
            val message = ChatMessage(
                id = UUID.randomUUID().toString(),
                schoolId = currentUser.schoolId,
                senderId = currentUser.id,
                senderName = currentUser.name,
                senderRole = currentUser.role.name,
                receiverId = receiverId,
                content = content,
                createdAt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
                isRead = false
            )
            repository.sendMessage(message)
        }
    }

    data class ChatDetailState(
        val messages: List<ChatMessage> = emptyList(),
        val receiverName: String = "",
        val currentUserId: String = "",
        val isLoading: Boolean = false
    )
}

