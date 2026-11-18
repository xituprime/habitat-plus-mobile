
package com.habitatplus.app.features.perfil.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.habitatplus.app.core.session.ResidentUser
import com.habitatplus.app.core.session.UserSessionRepository
import com.habitatplus.app.features.perfil.intent.ProfileIntent
import com.habitatplus.app.features.perfil.state.ProfileState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

class ProfileViewModel : ViewModel() {

    private val repository = UserSessionRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    private var loadedUid: String? = null
    private var requestVersion = 0
    private var isSaving = false

    init {
        loadProfile()
    }

    fun onIntent(intent: ProfileIntent) {
        when (intent) {
            ProfileIntent.Load -> loadProfile()

            ProfileIntent.ShowResidence ->
                _state.update {
                    it.copy(showResidence = true)
                }

            ProfileIntent.DismissResidence ->
                _state.update {
                    it.copy(showResidence = false)
                }

            ProfileIntent.StartEditing -> startEditing()

            is ProfileIntent.ChangeName ->
                _state.update {
                    if (it.isEditing && !isSaving) {
                        it.copy(
                            draftName = intent.value,
                            nameError = null
                        )
                    } else it
                }

            is ProfileIntent.ChangePhone ->
                _state.update {
                    if (it.isEditing && !isSaving) {
                        it.copy(
                            draftPhone = intent.value,
                            phoneError = null
                        )
                    } else it
                }

            ProfileIntent.SaveChanges -> saveChanges()

            ProfileIntent.CancelEditing -> {
                if (!isSaving) cancelEditing()
            }

            ProfileIntent.DismissSuccess ->
                _state.update {
                    it.copy(successMessage = null)
                }

            ProfileIntent.OpenSettings ->
                _state.update {
                    it.copy(settingsRequested = true)
                }

            ProfileIntent.SettingsNavigationHandled ->
                _state.update {
                    it.copy(settingsRequested = false)
                }
        }
    }

    fun clearSession() {
        requestVersion++
        loadedUid = null
        isSaving = false
        _state.value = ProfileState()
    }

    fun loadProfile() {
        val uid = auth.currentUser?.uid

        if (uid == null) {
            clearSession()
            _state.update {
                it.copy(
                    isLoading = false,
                    error = "Debes iniciar sesión."
                )
            }
            return
        }

        requestVersion++
        val request = requestVersion
        loadedUid = uid

        _state.value = ProfileState(isLoading = true)

        viewModelScope.launch {
            try {
                val user = repository.getCurrentUser()

                if (auth.currentUser?.uid != uid ||
                    loadedUid != uid ||
                    requestVersion != request
                ) return@launch

                showUser(user)

            } catch (e: Exception) {
                if (auth.currentUser?.uid == uid &&
                    loadedUid == uid &&
                    requestVersion == request
                ) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = "No se pudo cargar el perfil: " +
                                    (e.localizedMessage ?: "Error desconocido")
                        )
                    }
                }
            }
        }
    }

    private fun showUser(user: ResidentUser) {
        val approved =
            user.residenceStatus == "approved"

        val displayName = user.fullName.ifBlank {
            user.email.substringBefore("@")
        }

        _state.update {
            it.copy(
                isLoading = false,
                error = null,
                isOffline = false,
                name = displayName,
                initials = buildInitials(displayName),
                role = when (user.role) {
                    "admin" -> "Administrador"
                    else -> "Residente"
                },
                condominium = "Habitat+",
                tower = if (approved) {
                    "Unidad asignada"
                } else {
                    "Pendiente de asignación"
                },
                apartment = if (approved &&
                    user.apartment.isNotBlank()
                ) {
                    user.apartment
                } else {
                    "Sin asignar"
                },
                email = user.email,
                phone = user.phone,
                verified = approved
            )
        }
    }

    private fun startEditing() {
        _state.update { current ->
            if (current.isLoading ||
                current.error != null ||
                current.isEditing
            ) {
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
        if (!current.isEditing || isSaving) return

        val uid = auth.currentUser?.uid ?: return

        val name = current.draftName.trim()
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

        isSaving = true
        val request = ++requestVersion

        viewModelScope.launch {
            try {
                repository.updateProfile(name, phone)

                if (auth.currentUser?.uid != uid ||
                    requestVersion != request
                ) return@launch

                val updatedUser = repository.getCurrentUser()

                if (auth.currentUser?.uid != uid ||
                    requestVersion != request
                ) return@launch

                showUser(updatedUser)

                _state.update {
                    it.copy(
                        isEditing = false,
                        draftName = "",
                        draftPhone = "",
                        nameError = null,
                        phoneError = null,
                        successMessage =
                            "Los cambios de tu perfil se guardaron."
                    )
                }

            } catch (e: Exception) {
                if (auth.currentUser?.uid == uid &&
                    requestVersion == request
                ) {
                    _state.update {
                        it.copy(
                            phoneError = null,
                            error = null,
                            successMessage = null
                        )
                    }
                    // Conservamos el formulario para reintentar.
                    _state.update {
                        it.copy(
                            phoneError = "No se pudo guardar: " +
                                    (e.localizedMessage ?: "Error")
                        )
                    }
                }
            } finally {
                if (requestVersion == request) {
                    isSaving = false
                }
            }
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

    private fun validateName(name: String): String? =
        when {
            name.isBlank() -> "Ingresa tu nombre."
            name.length < 2 ->
                "El nombre debe tener al menos 2 caracteres."
            name.length > 80 ->
                "El nombre no puede superar 80 caracteres."
            name.none { it.isLetter() } ->
                "El nombre debe contener letras."
            else -> null
        }

    private fun validatePhone(phone: String): String? {
        if (phone.isBlank()) return null

        if (!Regex("^\\+?[0-9() -]+$").matches(phone)) {
            return "Formato de teléfono inválido."
        }

        val digits = phone.count { it.isDigit() }

        return if (digits !in 8..15) {
            "El teléfono debe tener entre 8 y 15 dígitos."
        } else null
    }

    private fun buildInitials(name: String): String =
        name.split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.take(1) }
            .uppercase(Locale.getDefault())
}
