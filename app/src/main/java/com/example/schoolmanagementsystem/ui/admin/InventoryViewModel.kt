package com.example.schoolmanagementsystem.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolmanagementsystem.domain.model.InventoryItem
import com.example.schoolmanagementsystem.domain.repository.InventoryRepository
import com.example.schoolmanagementsystem.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val repository: InventoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(InventoryState())
    val state = _state.asStateFlow()

    init {
        loadInventory()
    }

    fun loadInventory() {
        repository.getAllInventoryItems().onEach { result ->
            when (result) {
                is Resource.Loading -> _state.update { it.copy(isLoading = true) }
                is Resource.Success -> _state.update { 
                    it.copy(isLoading = false, items = result.data ?: emptyList()) 
                }
                is Resource.Error -> _state.update { 
                    it.copy(isLoading = false, error = result.message) 
                }
            }
        }.launchIn(viewModelScope)
    }

    fun addItem(name: String, category: String, quantity: Int) {
        viewModelScope.launch {
            val item = InventoryItem(
                id = UUID.randomUUID().toString(),
                schoolId = "", // Repository handles schoolId
                itemName = name,
                category = category,
                quantity = quantity,
                status = if (quantity > 0) "In Stock" else "Out of Stock",
                lastUpdated = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            )
            repository.addInventoryItem(item)
            loadInventory()
        }
    }

    data class InventoryState(
        val isLoading: Boolean = false,
        val items: List<InventoryItem> = emptyList(),
        val error: String? = null
    )
}
