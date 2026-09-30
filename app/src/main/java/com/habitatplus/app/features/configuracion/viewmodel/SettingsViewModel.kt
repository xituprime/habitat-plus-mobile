package com.habitatplus.app.features.configuracion.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.habitatplus.app.features.configuracion.intent.SettingsIntent
import com.habitatplus.app.features.configuracion.state.SettingsState
import com.habitatplus.app.features.configuracion.state.ThemePreference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SettingsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val preferences = application.getSharedPreferences(
        "habitat_resident_settings",
        Context.MODE_PRIVATE
    )

    private val initialTheme = runCatching {
        val savedTheme = preferences.getString(
            "theme",
            ThemePreference.SYSTEM.name
        )

        ThemePreference.valueOf(
            savedTheme ?: ThemePreference.SYSTEM.name
        )
    }.getOrDefault(ThemePreference.SYSTEM)

    private val _state = MutableStateFlow(
        SettingsState(
            theme = initialTheme
        )
    )

    val state = _state.asStateFlow()

    fun onIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.OpenDialog -> {
                _state.update {
                    it.copy(dialog = intent.dialog)
                }
            }

            SettingsIntent.DismissDialog -> {
                _state.update {
                    it.copy(dialog = null)
                }
            }

            is SettingsIntent.SelectTheme -> {
                preferences.edit()
                    .putString("theme", intent.theme.name)
                    .apply()

                _state.update {
                    it.copy(
                        theme = intent.theme,
                        dialog = null
                    )
                }
            }
        }
    }
}