package com.example.schoolmanagementsystem.ui.fee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolmanagementsystem.domain.model.FeeStructure
import com.example.schoolmanagementsystem.domain.repository.FeeRepository
import com.example.schoolmanagementsystem.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeeViewModel @Inject constructor(
    private val feeRepository: FeeRepository
) : ViewModel() {

    private val _state = MutableStateFlow(FeeState())
    val state = _state.asStateFlow()

    private val _saveState = MutableStateFlow<Resource<Unit>>(Resource.Success(Unit))
    val saveState = _saveState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

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
        ),
        val feeStructures: Resource<List<FeeStructure>> = Resource.Loading()
    )

    data class FeeItem(
        val title: String,
        val dueDate: String,
        val amount: String
    )

    fun onTabSelected(index: Int) {
        _state.value = _state.value.copy(selectedTab = index)
    }

    fun addFeeStructure(className: String, amount: String, dueDate: String, description: String) {
        viewModelScope.launch {
            _saveState.value = Resource.Loading()
            val feeStructure = FeeStructure(
                id = "",
                className = className,
                amount = amount.toDoubleOrNull() ?: 0.0,
                dueDate = dueDate,
                description = description
            )
            val result = feeRepository.addFeeStructure(feeStructure)
            _saveState.value = result
            if (result is Resource.Success) {
                _eventFlow.emit(UiEvent.SaveSuccess)
            } else if (result is Resource.Error) {
                _eventFlow.emit(UiEvent.ShowSnackbar(result.message ?: "Error saving fee structure"))
            }
        }
    }

    sealed class UiEvent {
        object SaveSuccess : UiEvent()
        data class ShowSnackbar(val message: String) : UiEvent()
    }
}
