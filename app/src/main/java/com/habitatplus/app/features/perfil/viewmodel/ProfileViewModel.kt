package com.habitatplus.app.features.perfil.viewmodel

import androidx.lifecycle.ViewModel
import com.habitatplus.app.features.perfil.intent.ProfileIntent
import com.habitatplus.app.features.perfil.state.ProfileState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale

class ProfileViewModel : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())

    val state: StateFlow<ProfileState> = _state.asStateFlow()

    private var profileLoaded = false

    init {
        onIntent(ProfileIntent.Load)
    }

    fun onIntent(intent: ProfileIntent) {
        when (intent) {
            ProfileIntent.Load -> loadProfile()

            ProfileIntent.ShowResidence -> {
                _state.update {
                    it.copy(showResidence = true)
                }
            }

            ProfileIntent.DismissResidence -> {
                _state.update {
                    it.copy(showResidence = false)
                }
            }

            ProfileIntent.StartEditing -> startEditing()

            is ProfileIntent.ChangeName -> {
                if (_state.value.isEditing) {
                    _state.update {
                        it.copy(
                            draftName = intent.value,
                            nameError = null
                        )
                    }
                }
            }

            is ProfileIntent.ChangePhone -> {
                if (_state.value.isEditing) {
                    _state.update {
                        it.copy(
                            draftPhone = intent.value,
                            phoneError = null
                        )
                    }
                }
            }

            ProfileIntent.SaveChanges -> saveChanges()

            ProfileIntent.CancelEditing -> cancelEditing()

            ProfileIntent.DismissSuccess -> {
                _state.update {
                    it.copy(successMessage = null)
                }
            }

            ProfileIntent.OpenSettings -> {
                _state.update {
                    it.copy(settingsRequested = true)
                }
            }

            ProfileIntent.SettingsNavigationHandled -> {
                _state.update {
                    it.copy(settingsRequested = false)
                }
            }
        }
    }

    private fun loadProfile() {
        // Evita sobrescribir una edición al volver a solicitar Load.
        if (profileLoaded) {
            return
        }

        // Datos fake para esta entrega.
        _state.value = ProfileState(
            isLoading = false,
            name = "Kenett Ortega",
            initials = "KO",
            role = "Residente",
            condominium = "Condominio Las Acacias",
            tower = "Torre A",
            apartment = "A-203",
            email = "kenett@example.com",
            phone = "+502 5555-0100",
            verified = true
        )

        profileLoaded = true
    }

    private fun startEditing() {
        val current = _state.value

        if (current.isLoading || current.error != null) {
            return
        }

        _state.update {
            it.copy(
                isEditing = true,
                draftName = it.name,
                draftPhone = it.phone,
                nameError = null,
                phoneError = null,
                successMessage = null,
                showResidence = false
            )
        }
    }

    private fun saveChanges() {
        val current = _state.value

        if (!current.isEditing) {
            return
        }

        val normalizedName = current.draftName
            .trim()
            .replace(Regex("\\s+"), " ")

        val normalizedPhone = current.draftPhone.trim()

        val nameError = validateName(normalizedName)
        val phoneError = validatePhone(normalizedPhone)

        if (nameError != null || phoneError != null) {
            _state.update {
                it.copy(
                    nameError = nameError,
                    phoneError = phoneError
                )
            }
            return
        }

        _state.update {
            it.copy(
                name = normalizedName,
                initials = buildInitials(normalizedName),
                phone = normalizedPhone,

                isEditing = false,
                draftName = "",
                draftPhone = "",
                nameError = null,
                phoneError = null,

                successMessage = "Los cambios de tu perfil se guardaron."
            )
        }
    }

    private fun cancelEditing() {
        _state.update {
            it.copy(
                isEditing = false,
                draftName = "",
                draftPhone = "",
                nameError = null,
                phoneError = null
            )
        }
    }

    private fun validateName(name: String): String? {
        return when {
            name.isBlank() ->
                "Ingresa tu nombre."

            name.length < 2 ->
                "El nombre debe tener al menos 2 caracteres."

            name.length > 80 ->
                "El nombre no puede superar 80 caracteres."

            name.none { it.isLetter() } ->
                "El nombre debe contener letras."

            else -> null
        }
    }

    private fun validatePhone(phone: String): String? {
        // El teléfono es opcional según la especificación.
        if (phone.isBlank()) {
            return null
        }

        val allowedFormat = Regex("^\\+?[0-9() -]+$")

        if (!allowedFormat.matches(phone)) {
            return "Usa números, espacios, guiones o paréntesis. " +
                    "El signo + solo puede ir al inicio."
        }

        val digitCount = phone.count { it in '0'..'9' }

        return if (digitCount !in 8..15) {
            "El teléfono debe contener entre 8 y 15 dígitos."
        } else {
            null
        }
    }

    private fun buildInitials(name: String): String {
        return name
            .split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.take(1) }
            .uppercase(Locale.getDefault())
    }
}