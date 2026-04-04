package com.example.schoolmanagementsystem.frontend.ui.gallery

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(GalleryState())
    val state = _state.asStateFlow()

    data class GalleryState(
        val albums: List<AlbumItem> = listOf(
            AlbumItem("Gold Medalist of SOF (English) - 2025 - 26", 8),
            AlbumItem("Republic Day Celebration 2026.", 34),
            AlbumItem("IAAPL 8th National Abacus Talent Hunt - 2025", 6),
            AlbumItem("Nursery Fancy Dress Competition.", 39)
        )
    )

    data class AlbumItem(
        val title: String,
        val photoCount: Int,
        val coverUrl: String? = null
    )
}

