package com.example.schoolmanagementsystem.frontend.ui.library

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(LibraryState())
    val state = _state.asStateFlow()

    data class LibraryState(
        val borrowedCount: Int = 0,
        val returnedCount: Int = 0,
        val searchBooksResults: List<BookItem> = emptyList()
    )

    data class BookItem(
        val title: String,
        val author: String,
        val status: String
    )
}

