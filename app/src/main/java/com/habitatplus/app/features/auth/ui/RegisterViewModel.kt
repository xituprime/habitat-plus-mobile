package com.habitatplus.app.features.auth.ui

import android.util.Patterns
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
import java.util.Locale

class RegisterViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.NameChanged -> _state.update { it.copy(name = event.value, error = null) }
            is RegisterEvent.LastNameChanged -> _state.update { it.copy(lastName = event.value, error = null) }
            is RegisterEvent.EmailChanged -> _state.update { it.copy(email = event.value, error = null) }
            is RegisterEvent.PhoneChanged -> _state.update { it.copy(phone = event.value, error = null) }
            is RegisterEvent.CondominiumChanged -> _state.update { it.copy(condominium = event.value, error = null) }
            is RegisterEvent.TowerChanged -> _state.update { it.copy(tower = event.value, error = null) }
            is RegisterEvent.ApartmentChanged -> _state.update {
                it.copy(apartment = event.value.uppercase(Locale.ROOT).replace(" ", ""), error = null)
            }
            is RegisterEvent.PasswordChanged -> _state.update { it.copy(password = event.value, error = null) }
            is RegisterEvent.ConfirmPasswordChanged -> _state.update { it.copy(confirmPassword = event.value, error = null) }
            RegisterEvent.RegisterClicked -> register()
            RegisterEvent.ClearError -> _state.update { it.copy(error = null) }
        }
    }


    private fun register() {
        val current = _state.value
        if (current.isLoading || current.isRegistered) return

        val name = current.name.trim()
        val lastName = current.lastName.trim()
        val email = current.email.trim()
        val phone = current.phone.trim()
        val apartment = current.apartment.trim()
            .uppercase(Locale.ROOT)

        val password = current.password
        val confirmPassword = current.confirmPassword

        val error = when {
            name.isBlank() || lastName.isBlank() ||
                    email.isBlank() || apartment.isBlank() ||
                    password.isBlank() || confirmPassword.isBlank() ->
                "Completa todos los campos obligatorios."

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                "Ingresa un correo electrónico válido."

            name.length < 2 || lastName.length < 2 ->
                "El nombre y apellido deben tener al menos 2 caracteres."

            !Regex("^[0-9]{1,4}-[A-Z]{1,3}$").matches(apartment) ->
                "Escribe el apartamento con formato 206-A."

            password.length < 8 ->
                "La contraseña debe tener al menos 8 caracteres."

            password != confirmPassword ->
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
                val result = auth
                    .createUserWithEmailAndPassword(email, password)
                    .await()

                val user = result.user
                    ?: throw IllegalStateException(
                        "No se pudo obtener el usuario."
                    )

                val userData = hashMapOf<String, Any>(
                    "uid" to user.uid,
                    "name" to name,
                    "lastName" to lastName,
                    "email" to email,
                    "phone" to phone,
                    "requestedApartment" to apartment,
                    "apartment" to "",
                    "residenceStatus" to "pending",
                    "role" to "resident",
                    "photoUrl" to "",
                    "createdAt" to FieldValue.serverTimestamp()
                )

                try {
                    firestore.collection("users")
                        .document(user.uid)
                        .set(userData)
                        .await()

                } catch (writeError: Exception) {
                    // Recuperar solamente si el perfil ya existe
                    // y pertenece al usuario autenticado.
                    val snapshot = firestore.collection("users")
                        .document(user.uid)
                        .get(com.google.firebase.firestore.Source.SERVER)
                        .await()

                    val validProfile =
                        snapshot.exists() &&
                                snapshot.getString("uid") == user.uid &&
                                snapshot.getString("email")
                                    ?.equals(email, ignoreCase = true) == true &&
                                snapshot.getString("residenceStatus") == "pending"

                    if (!validProfile) {
                        throw writeError
                    }
                }

                // El perfil fue guardado o verificado.
                if (auth.currentUser?.uid == user.uid) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRegistered = true,
                            error = null
                        )
                    }
                }

            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        isRegistered = false,
                        error = e.localizedMessage
                            ?: "No se pudo completar el registro."
                    )
                }
            }
        }
    }

}
