package com.example.schoolmanagementsystem.ui.myclass

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MyClassViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(MyClassState())
    val state = _state.asStateFlow()

    data class MyClassState(
        val attendancePercentage: String = "88.54%",
        val timetableClasses: String = "8 classes",
        val subjectsCount: Int = 18
    )
}
