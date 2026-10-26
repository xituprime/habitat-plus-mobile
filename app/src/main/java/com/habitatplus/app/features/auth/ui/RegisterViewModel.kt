package com.habitatplus.app.features.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.NameChanged ->
                _state.update { it.copy(name = event.value, error = null) }

            is RegisterEvent.EmailChanged ->
                _state.update { it.copy(email = event.value, error = null) }

            is RegisterEvent.PasswordChanged ->
                _state.update { it.copy(password = event.value, error = null) }

            is RegisterEvent.ConfirmPasswordChanged ->
                _state.update {
                    it.copy(confirmPassword = event.value, error = null)
                }
            is RegisterEvent.LastNameChanged ->
                _state.update {
                    it.copy(lastName = event.value, error = null)
                }

            is RegisterEvent.PhoneChanged ->
                _state.update {
                    it.copy(phone = event.value, error = null)
                }

            is RegisterEvent.ApartmentChanged ->
                _state.update {
                    it.copy(apartment = event.value, error = null)
                }

            RegisterEvent.RegisterClicked -> register()

            RegisterEvent.ClearError ->
                _state.update { it.copy(error = null) }
        }
    }

    private fun register() {
        val current = _state.value

        if (current.isLoading || current.isRegistered) return

        val error = when {
            current.name.isBlank() ||
                    current.email.isBlank() ||
                    current.password.isBlank() ||
                    current.lastName.isBlank() ||
                    current.apartment.isBlank() ||
                    current.confirmPassword.isBlank() ->
                "Completa todos los campos."

            !android.util.Patterns.EMAIL_ADDRESS
                .matcher(current.email.trim()).matches() ->
                "Ingresa un correo válido."

            current.password.length < 8 ->
                "La contraseña debe tener al menos 8 caracteres."

            current.password != current.confirmPassword ->
                "Las contraseñas no coinciden."

            else -> null
        }

        if (error != null) {
            _state.update { it.copy(error = error) }
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true, error = null)
            }

            delay(500)

            // Registro simulado: no crea una cuenta real.
            _state.update {
                it.copy(
                    isLoading = false,
                    isRegistered = true
                )
            }
        }
    }
}
