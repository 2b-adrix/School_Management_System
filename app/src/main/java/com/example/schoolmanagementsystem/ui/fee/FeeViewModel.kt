package com.example.schoolmanagementsystem.ui.fee

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class FeeViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(FeeState())
    val state = _state.asStateFlow()

    data class FeeState(
        val selectedTab: Int = 0,
        val totalDue: String = "₹ 26175.00",
        val dueFees: List<FeeItem> = listOf(
            FeeItem("2nd Installment", "15 July 2025", "₹ 8725.00"),
            FeeItem("3rd Installment", "15 October 2025", "₹ 8725.00"),
            FeeItem("4th Installment", "15 January 2026", "₹ 8725.00")
        ),
        val paidFees: List<FeeItem> = listOf(
            FeeItem("1st Installment", "15 April 2025", "₹ 8725.00")
        )
    )

    data class FeeItem(
        val title: String,
        val dueDate: String,
        val amount: String
    )

    fun onTabSelected(index: Int) {
        _state.value = _state.value.copy(selectedTab = index)
    }
}
