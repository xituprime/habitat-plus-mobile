package com.habitatplus.app.features.perfil.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitatplus.app.core.data.FakeResidentRepository
import com.habitatplus.app.core.data.Resident
import com.habitatplus.app.core.data.ResidentRepository
import com.habitatplus.app.features.perfil.intent.ProfileIntent
import com.habitatplus.app.features.perfil.state.ProfileState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

class ProfileViewModel : ViewModel() {

    private val repository: ResidentRepository = FakeResidentRepository

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        loadProfile()

        viewModelScope.launch {
            repository.resident.collect { resident ->
                showResident(resident)
            }
        }
    }

    fun onIntent(intent: ProfileIntent) {
        when (intent) {
            ProfileIntent.Load -> loadProfile()

            ProfileIntent.ShowResidence -> {
                _state.update { it.copy(showResidence = true) }
            }

            ProfileIntent.DismissResidence -> {
                _state.update { it.copy(showResidence = false) }
            }

            ProfileIntent.StartEditing -> startEditing()

            is ProfileIntent.ChangeName -> {
                _state.update { current ->
                    if (current.isEditing) {
                        current.copy(
                            draftName = intent.value,
                            nameError = null
                        )
                    } else {
                        current
                    }
                }
            }

            is ProfileIntent.ChangePhone -> {
                _state.update { current ->
                    if (current.isEditing) {
                        current.copy(
                            draftPhone = intent.value,
                            phoneError = null
                        )
                    } else {
                        current
                    }
                }
            }

            ProfileIntent.SaveChanges -> saveChanges()

            ProfileIntent.CancelEditing -> cancelEditing()

            ProfileIntent.DismissSuccess -> {
                _state.update { it.copy(successMessage = null) }
            }

            ProfileIntent.OpenSettings -> {
                _state.update { it.copy(settingsRequested = true) }
            }

            ProfileIntent.SettingsNavigationHandled -> {
                _state.update { it.copy(settingsRequested = false) }
            }
        }
    }

    private fun loadProfile() {
        showResident(repository.resident.value)
    }

    private fun showResident(resident: Resident) {
        _state.update { current ->
            current.copy(
                isLoading = false,
                error = null,
                isOffline = false,
                name = resident.name,
                initials = buildInitials(resident.name),
                role = resident.role,
                condominium = resident.condominium,
                tower = resident.tower,
                apartment = resident.apartment,
                email = resident.email,
                phone = resident.phone,
                verified = resident.verified
            )
        }
    }

    private fun startEditing() {
        _state.update { current ->
            if (current.isLoading || current.error != null || current.isEditing) {
                current
            } else {
                current.copy(
                    isEditing = true,
                    draftName = current.name,
                    draftPhone = current.phone,
                    nameError = null,
                    phoneError = null,
                    successMessage = null,
                    showResidence = false
                )
            }
        }
    }

    private fun saveChanges() {
        val current = _state.value

        if (!current.isEditing) return

        val name = current.draftName
            .trim()
            .replace(Regex("\\s+"), " ")

        val phone = current.draftPhone.trim()

        val nameError = validateName(name)
        val phoneError = validatePhone(phone)

        if (nameError != null || phoneError != null) {
            _state.update {
                it.copy(
                    nameError = nameError,
                    phoneError = phoneError
                )
            }
            return
        }

        repository.updateContact(name = name, phone = phone)

        // Refleja el resultado inmediatamente en Perfil.
        showResident(repository.resident.value)

        _state.update {
            it.copy(
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
        if (phone.isBlank()) return null

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