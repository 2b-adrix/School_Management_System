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
            MessageItem("1", "Biju", "admin", "Notice Regarding Sched...", "10:45 AM", 1, true),
            MessageItem("2", "Biju", "admin", "होली की शुभकामनाएँ", "Yesterday", 1, false),
            MessageItem("3", "Biju", "admin", "NOTICE REGARDING SUSP...", "9 days ago", 0, false),
            MessageItem("4", "Biju", "admin", "Notice Regarding Publicati...", "10 days ago", 0, true),
            MessageItem("5", "Biju", "admin", "Notice Regarding Holiday (...", "18 days ago", 0, false),
            MessageItem("6", "Biju", "admin", "...", "a month ago", 0, false)
        )
    )

    data class MessageItem(
        val id: String,
        val sender: String,
        val role: String,
        val content: String,
        val time: String,
        val unreadCount: Int,
        val isOnline: Boolean = false
    )
}

