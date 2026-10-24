package com.habitatplus.app.features.configuracion.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.habitatplus.app.features.configuracion.intent.SettingsIntent
import com.habitatplus.app.features.configuracion.state.SettingsDialog
import com.habitatplus.app.features.configuracion.state.SettingsState
import com.habitatplus.app.features.configuracion.state.ThemePreference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SettingsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val preferences = application.getSharedPreferences(
        "habitat_resident_settings",
        Context.MODE_PRIVATE
    )

    private val _state = MutableStateFlow(
        SettingsState(theme = readSavedTheme())
    )

    val state: StateFlow<SettingsState> = _state.asStateFlow()

    fun onIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.OpenDialog -> {
                _state.update { current ->
                    if (current.signOutRequested) {
                        current
                    } else {
                        current.copy(dialog = intent.dialog)
                    }
                }
            }

            SettingsIntent.DismissDialog -> {
                _state.update { current ->
                    current.copy(dialog = null)
                }
            }

            is SettingsIntent.SelectTheme -> {
                saveTheme(intent.theme)
            }

            SettingsIntent.ConfirmSignOut -> {
                _state.update { current ->
                    if (current.dialog == SettingsDialog.SIGN_OUT) {
                        current.copy(
                            dialog = null,
                            signOutRequested = true
                        )
                    } else {
                        current
                    }
                }
            }

            SettingsIntent.SignOutHandled -> {
                _state.update { current ->
                    current.copy(signOutRequested = false)
                }
            }
        }
    }

    private fun readSavedTheme(): ThemePreference {
        val savedTheme = preferences.getString(
            "theme",
            ThemePreference.SYSTEM.name
        )

        return ThemePreference.entries.firstOrNull {
            it.name == savedTheme
        } ?: ThemePreference.SYSTEM
    }

    private fun saveTheme(theme: ThemePreference) {
        preferences.edit()
            .putString("theme", theme.name)
            .apply()

        _state.update { current ->
            current.copy(
                theme = theme,
                dialog = null
            )
        }
    }
}