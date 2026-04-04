package com.example.schoolmanagementsystem.backend.data.manager

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.schoolmanagementsystem.backend.domain.model.UserRole
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "session_prefs")

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val USER_ROLE = stringPreferencesKey("user_role")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val SCHOOL_ID = stringPreferencesKey("school_id")
        private val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
        private val THEME_MODE = stringPreferencesKey("theme_mode")
    }

    suspend fun saveSession(name: String, email: String, role: UserRole, schoolId: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_NAME] = name
            prefs[USER_EMAIL] = email
            prefs[USER_ROLE] = role.name
            prefs[SCHOOL_ID] = schoolId
        }
    }

    val userRole: Flow<UserRole?> = context.dataStore.data.map { prefs ->
        prefs[USER_ROLE]?.let { UserRole.valueOf(it) }
    }

    val userName: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[USER_NAME]
    }
    
    val schoolId: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[SCHOOL_ID]
    }

    val isBiometricEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[BIOMETRIC_ENABLED] ?: false
    }

    val themeMode: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[THEME_MODE] ?: "SYSTEM"
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { prefs ->
            prefs[THEME_MODE] = mode
        }
    }

    suspend fun setBiometricEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[BIOMETRIC_ENABLED] = enabled
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }
}

