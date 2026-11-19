
package com.habitatplus.app.features.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RegisterViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.NameChanged ->
                _state.update {
                    it.copy(name = event.value, error = null)
                }

            is RegisterEvent.LastNameChanged ->
                _state.update {
                    it.copy(lastName = event.value, error = null)
                }

            is RegisterEvent.EmailChanged ->
                _state.update {
                    it.copy(email = event.value, error = null)
                }

            is RegisterEvent.PhoneChanged ->
                _state.update {
                    it.copy(phone = event.value, error = null)
                }

            is RegisterEvent.ApartmentChanged ->
                _state.update {
                    it.copy(apartment = event.value, error = null)
                }

            is RegisterEvent.PasswordChanged ->
                _state.update {
                    it.copy(password = event.value, error = null)
                }

            is RegisterEvent.ConfirmPasswordChanged ->
                _state.update {
                    it.copy(confirmPassword = event.value, error = null)
                }

            RegisterEvent.RegisterClicked -> register()

            RegisterEvent.ClearError ->
                _state.update { it.copy(error = null) }
        }
    }

    private fun register() {
        val current = _state.value

        if (current.isLoading || current.isRegistered) return

        val email = current.email.trim()

        val error = when {
            current.name.isBlank() ||
                    current.lastName.isBlank() ||
                    email.isBlank() ||
                    current.apartment.isBlank() ||
                    current.password.isBlank() ||
                    current.confirmPassword.isBlank() ->
                "Completa todos los campos obligatorios."

            !android.util.Patterns.EMAIL_ADDRESS
                .matcher(email).matches() ->
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

        _state.update {
            it.copy(isLoading = true, error = null)
        }

        viewModelScope.launch {
            try {
                val result =
                    auth.createUserWithEmailAndPassword(
                        email,
                        current.password
                    ).await()

                val user = result.user
                    ?: throw IllegalStateException(
                        "No se pudo obtener el usuario creado."
                    )

                val userData = hashMapOf(
                    "uid" to user.uid,
                    "name" to current.name.trim(),
                    "lastName" to current.lastName.trim(),
                    "email" to email,
                    "phone" to current.phone.trim(),

                    // La vivienda solicitada no está aprobada.
                    "requestedApartment" to current.apartment.trim(),
                    "apartment" to "",
                    "residenceStatus" to "pending",

                    "role" to "resident",
                    "photoUrl" to "",
                    "createdAt" to FieldValue.serverTimestamp()
                )

                firestore.collection("users")
                    .document(user.uid)
                    .set(userData)
                    .await()

                _state.update {
                    it.copy(
                        isLoading = false,
                        isRegistered = true,
                        error = null
                    )
                }

            } catch (exception: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        isRegistered = false,
                        error = exception.localizedMessage
                            ?: "No se pudo crear la cuenta."
                    )
                }
            }
        }
    }
}
