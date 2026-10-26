
package com.habitatplus.app.features.auth.ui

sealed interface RegisterEvent {
    data class NameChanged(val value: String) : RegisterEvent
    data class LastNameChanged(val value: String) : RegisterEvent
    data class EmailChanged(val value: String) : RegisterEvent
    data class PhoneChanged(val value: String) : RegisterEvent
    data class ApartmentChanged(val value: String) : RegisterEvent
    data class PasswordChanged(val value: String) : RegisterEvent
    data class ConfirmPasswordChanged(val value: String) : RegisterEvent

    data object RegisterClicked : RegisterEvent
    data object ClearError : RegisterEvent
}
