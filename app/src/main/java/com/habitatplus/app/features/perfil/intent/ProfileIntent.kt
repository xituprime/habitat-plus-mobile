package com.habitatplus.app.features.perfil.intent

sealed interface ProfileIntent {

    data object Load : ProfileIntent

    data object ShowResidence : ProfileIntent

    data object DismissResidence : ProfileIntent

    data object StartEditing : ProfileIntent

    data class ChangeName(
        val value: String
    ) : ProfileIntent

    data class ChangePhone(
        val value: String
    ) : ProfileIntent

    data object SaveChanges : ProfileIntent

    data object CancelEditing : ProfileIntent

    data object DismissSuccess : ProfileIntent

    data object OpenSettings : ProfileIntent

    data object SettingsNavigationHandled : ProfileIntent
}