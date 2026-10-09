package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AppSettings(
    val simplifiedMode: Boolean = false,
    val speakNamesAloud: Boolean = true,
    val gridColumns: Int = 3,
    val darkThemeMode: Int = 1 // 0: system, 1: light (warm cream royal), 2: dark
)

class SettingsRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("queen_phone_settings", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    private fun loadSettings(): AppSettings {
        return AppSettings(
            simplifiedMode = prefs.getBoolean("simplified_mode", false),
            speakNamesAloud = prefs.getBoolean("speak_names_aloud", true),
            gridColumns = prefs.getInt("grid_columns", 3),
            darkThemeMode = prefs.getInt("dark_theme_mode", 1)
        )
    }

    fun updateSimplifiedMode(enabled: Boolean) {
        prefs.edit().putBoolean("simplified_mode", enabled).apply()
        _settings.value = _settings.value.copy(simplifiedMode = enabled)
    }

    fun updateSpeakNamesAloud(enabled: Boolean) {
        prefs.edit().putBoolean("speak_names_aloud", enabled).apply()
        _settings.value = _settings.value.copy(speakNamesAloud = enabled)
    }

    fun updateGridColumns(columns: Int) {
        val safeColumns = if (columns in 2..3) columns else 3
        prefs.edit().putInt("grid_columns", safeColumns).apply()
        _settings.value = _settings.value.copy(gridColumns = safeColumns)
    }

    fun updateDarkThemeMode(mode: Int) {
        prefs.edit().putInt("dark_theme_mode", mode).apply()
        _settings.value = _settings.value.copy(darkThemeMode = mode)
    }

    fun resetDefaults() {
        prefs.edit().clear().apply()
        _settings.value = AppSettings()
    }
}
