package com.example.schoolmanagementsystem.frontend.ui.message

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MessagesViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(MessagesState())
    val state = _state.asStateFlow()

    fun onSearchQueryChange(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
    }

    data class MessagesState(
        val searchQuery: String = "",
        val messages: List<MessageItem> = listOf(
            MessageItem("Biju", "admin", "Notice Regarding Sched...", "10:45 AM", 1, true),
            MessageItem("Biju", "admin", "होली की शुभकामनाएँ", "Yesterday", 1, false),
            MessageItem("Biju", "admin", "NOTICE REGARDING SUSP...", "9 days ago", 0, false),
            MessageItem("Biju", "admin", "Notice Regarding Publicati...", "10 days ago", 0, true),
            MessageItem("Biju", "admin", "Notice Regarding Holiday (...", "18 days ago", 0, false),
            MessageItem("Biju", "admin", "...", "a month ago", 0, false)
        )
    )

    data class MessageItem(
        val sender: String,
        val role: String,
        val content: String,
        val time: String,
        val unreadCount: Int,
        val isOnline: Boolean = false
    )
}

