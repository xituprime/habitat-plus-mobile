package com.habitatplus.app.features.configuracion.intent

import com.habitatplus.app.features.configuracion.state.SettingsDialog
import com.habitatplus.app.features.configuracion.state.ThemePreference

sealed interface SettingsIntent {

    data class OpenDialog(
        val dialog: SettingsDialog
    ) : SettingsIntent

    data object DismissDialog : SettingsIntent

    data class SelectTheme(
        val theme: ThemePreference
    ) : SettingsIntent
}