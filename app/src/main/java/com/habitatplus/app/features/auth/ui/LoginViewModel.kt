
package com.habitatplus.app.features.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged ->
                _state.update {
                    it.copy(email = event.value, error = null)
                }

            is LoginEvent.PasswordChanged ->
                _state.update {
                    it.copy(password = event.value, error = null)
                }

            LoginEvent.LoginClicked -> login()

            LoginEvent.ClearError ->
                _state.update { it.copy(error = null) }
        }
    }

    private fun login() {
        val current = _state.value

        if (current.isLoading || current.isLoggedIn) return

        val email = current.email.trim()
        val password = current.password

        if (email.isBlank() || password.isBlank()) {
            _state.update {
                it.copy(error = "Completa todos los campos.")
            }
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS
                .matcher(email).matches()
        ) {
            _state.update {
                it.copy(error = "Ingresa un correo válido.")
            }
            return
        }

        _state.update {
            it.copy(isLoading = true, error = null)
        }

        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(
                    email,
                    password
                ).await()

                _state.update {
                    it.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        error = null
                    )
                }
            } catch (exception: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        isLoggedIn = false,
                        error = "Correo o contraseña incorrectos, o no se pudo conectar."
                    )
                }
            }
        }
    }
}
