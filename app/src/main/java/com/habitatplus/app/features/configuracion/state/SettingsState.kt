package com.habitatplus.app.features.configuracion.state

enum class ThemePreference(val label: String) {
    SYSTEM("Predeterminado del sistema"),
    LIGHT("Claro"),
    DARK("Oscuro")
}

enum class SettingsDialog {
    LANGUAGE,
    THEME,
    ABOUT,
    SIGN_OUT
}

data class SettingsState(
    val theme: ThemePreference = ThemePreference.SYSTEM,
    val dialog: SettingsDialog? = null
)