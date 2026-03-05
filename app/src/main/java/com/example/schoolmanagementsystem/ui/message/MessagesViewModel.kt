package com.example.schoolmanagementsystem.ui.message

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MessagesViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(MessagesState())
    val state = _state.asStateFlow()

    data class MessagesState(
        val messages: List<MessageItem> = listOf(
            MessageItem("Biju", "admin", "Notice Regarding Sched...", "a day ago", 1),
            MessageItem("Biju", "admin", "होली की शुभकामनाएँ", "a day ago", 1),
            MessageItem("Biju", "admin", "NOTICE REGARDING SUSP...", "9 days ago", 0),
            MessageItem("Biju", "admin", "Notice Regarding Publicati...", "10 days ago", 0),
            MessageItem("Biju", "admin", "Notice Regarding Holiday (...", "18 days ago", 0),
            MessageItem("Biju", "admin", "...", "a month ago", 0)
        )
    )

    data class MessageItem(
        val sender: String,
        val role: String,
        val content: String,
        val time: String,
        val unreadCount: Int
    )
}
