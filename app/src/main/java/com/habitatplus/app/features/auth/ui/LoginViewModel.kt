package com.habitatplus.app.features.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _state = MutableStateFlow(LoginState())

    val state: StateFlow<LoginState> =
        _state.asStateFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> {
                _state.update {
                    it.copy(
                        email = event.value,
                        error = null
                    )
                }
            }

            is LoginEvent.PasswordChanged -> {
                _state.update {
                    it.copy(
                        password = event.value,
                        error = null
                    )
                }
            }

            LoginEvent.LoginClicked -> login()

            LoginEvent.ClearError -> {
                _state.update {
                    it.copy(error = null)
                }
            }
        }
    }

    private fun login() {
        val current = _state.value

        if (current.isLoading || current.isLoggedIn) return

        if (current.email.isBlank() ||
            current.password.isBlank()
        ) {
            _state.update {
                it.copy(
                    error = "Completa todos los campos."
                )
            }
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS
                .matcher(current.email.trim()).matches()
        ) {
            _state.update {
                it.copy(
                    error = "Ingresa un correo válido."
                )
            }
            return
        }

        if (current.password.length < 8) {
            _state.update {
                it.copy(
                    error = "La contraseña debe tener al menos 8 caracteres."
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }

            delay(500)

            // Autenticación simulada para el entregable.
            // No valida credenciales contra un servidor.
            _state.update {
                it.copy(
                    isLoading = false,
                    isLoggedIn = true
                )
            }
        }
    }
}
