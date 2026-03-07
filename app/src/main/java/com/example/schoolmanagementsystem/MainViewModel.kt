package com.example.schoolmanagementsystem

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolmanagementsystem.data.local.datastore.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    val themeMode: StateFlow<String> = userPreferences.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "system")

    val languageCode: StateFlow<String> = userPreferences.languageCode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "en")

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            userPreferences.saveThemeMode(mode)
        }
    }

    fun setLanguageCode(code: String) {
        viewModelScope.launch {
            userPreferences.saveLanguageCode(code)
        }
    }
}
