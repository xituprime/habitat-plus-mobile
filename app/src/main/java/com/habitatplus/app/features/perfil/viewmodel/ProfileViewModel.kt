
package com.habitatplus.app.features.perfil.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.habitatplus.app.features.perfil.intent.ProfileIntent
import com.habitatplus.app.features.perfil.state.ProfileState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Locale

class ProfileViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _state = MutableStateFlow(ProfileState())

    val state: StateFlow<ProfileState> =
        _state.asStateFlow()

    private var profileJob: Job? = null
    private var saveJob: Job? = null
    private var loadedUserId: String? = null

    private val authListener =
        FirebaseAuth.AuthStateListener { firebaseAuth ->

            val userId = firebaseAuth.currentUser?.uid

            if (userId != loadedUserId) {
                profileJob?.cancel()
                saveJob?.cancel()

                loadedUserId = userId
                _state.value = ProfileState()

                if (userId != null) {
                    loadProfile()
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = "Debes iniciar sesión."
                        )
                    }
                }
            }
        }

    init {
        auth.addAuthStateListener(authListener)
        loadProfile()
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
                _state.update { current ->
                    if (current.isEditing && !current.isLoading) {
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
                    if (current.isEditing && !current.isLoading) {
                        current.copy(
                            draftPhone = intent.value,
                            phoneError = null
                        )
                    } else {
                        current
                    }
                }
            }

            is ProfileIntent.ChangeCondominium -> {
                _state.update { current ->
                    if (current.isEditing && !current.isLoading) {
                        current.copy(
                            draftCondominium = intent.value,
                            condominiumError = null
                        )
                    } else {
                        current
                    }
                }
            }

            is ProfileIntent.ChangeTower -> {
                _state.update { current ->
                    if (current.isEditing && !current.isLoading) {
                        current.copy(
                            draftTower = intent.value,
                            towerError = null
                        )
                    } else {
                        current
                    }
                }
            }

            is ProfileIntent.ChangeApartment -> {
                _state.update { current ->
                    if (current.isEditing && !current.isLoading) {
                        current.copy(
                            draftApartment = intent.value,
                            apartmentError = null
                        )
                    } else {
                        current
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
        val user = auth.currentUser

        if (user == null) {
            profileJob?.cancel()
            loadedUserId = null

            _state.value = ProfileState(
                isLoading = false,
                error = "Debes iniciar sesión."
            )
            return
        }

        val userId = user.uid

        profileJob?.cancel()
        loadedUserId = userId

        _state.update {
            it.copy(
                isLoading = true,
                error = null,
                isEditing = false
            )
        }

        profileJob = viewModelScope.launch {
            try {
                val document = firestore
                    .collection("users")
                    .document(userId)
                    .get()
                    .await()

                if (auth.currentUser?.uid != userId) {
                    return@launch
                }

                if (!document.exists()) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = "No se encontró tu perfil."
                        )
                    }
                    return@launch
                }

                val name = document
                    .getString("name")
                    .orEmpty()
                    .trim()

                val lastName = document
                    .getString("lastName")
                    .orEmpty()
                    .trim()

                val fullName = listOf(name, lastName)
                    .filter { it.isNotBlank() }
                    .joinToString(" ")
                    .ifBlank { "Residente" }

                val apartment = document
                    .getString("apartment")
                    .orEmpty()
                    .trim()

                val condominium = document
                    .getString("condominium")
                    .orEmpty()
                    .trim()

                val tower = document
                    .getString("tower")
                    .orEmpty()
                    .trim()

                val phone = document
                    .getString("phone")
                    .orEmpty()
                    .trim()

                val email = document
                    .getString("email")
                    ?.takeIf { it.isNotBlank() }
                    ?: user.email.orEmpty()

                val role = document
                    .getString("role")
                    .orEmpty()
                    .ifBlank { "resident" }

                val verified = document
                    .getBoolean("verified") ?: false

                _state.update {
                    it.copy(
                        isLoading = false,
                        error = null,
                        isOffline = false,
                        name = fullName,
                        initials = buildInitials(fullName),
                        role = role,
                        condominium = condominium,
                        tower = tower,
                        apartment = apartment,
                        email = email,
                        phone = phone,
                        verified = verified,
                        isEditing = false
                    )
                }

            } catch (exception: Exception) {
                if (auth.currentUser?.uid == userId) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = "No se pudo cargar tu perfil."
                        )
                    }
                }
            }
        }
    }

    private fun startEditing() {
        _state.update { current ->
            if (
                current.isLoading ||
                current.error != null ||
                current.isEditing
            ) {
                current
            } else {
                current.copy(
                    isEditing = true,
                    draftName = current.name,
                    draftPhone = current.phone,
                    draftCondominium = current.condominium,
                    draftTower = current.tower,
                    draftApartment = current.apartment,
                    nameError = null,
                    phoneError = null,
                    condominiumError = null,
                    towerError = null,
                    apartmentError = null,
                    successMessage = null,
                    showResidence = false
                )
            }
        }
    }

    private fun saveChanges() {
        val current = _state.value

        if (!current.isEditing || current.isLoading) {
            return
        }

        val userId = auth.currentUser?.uid

        if (userId.isNullOrBlank()) {
            _state.update {
                it.copy(error = "Debes iniciar sesión.")
            }
            return
        }

        val name = current.draftName
            .trim()
            .replace(Regex("\\s+"), " ")

        val phone = current.draftPhone.trim()

        val condominium = current.draftCondominium.trim()
        val tower = current.draftTower.trim()
        val apartment = current.draftApartment.trim()

        val nameError = validateName(name)
        val phoneError = validatePhone(phone)

        val condominiumError =
            validateResidenceField(
                condominium,
                "condominio",
                100
            )

        val towerError =
            validateResidenceField(
                tower,
                "torre",
                50
            )

        val apartmentError =
            validateResidenceField(
                apartment,
                "apartamento",
                30
            )

        if (
            nameError != null ||
            phoneError != null ||
            condominiumError != null ||
            towerError != null ||
            apartmentError != null
        ) {
            _state.update {
                it.copy(
                    nameError = nameError,
                    phoneError = phoneError,
                    condominiumError = condominiumError,
                    towerError = towerError,
                    apartmentError = apartmentError
                )
            }
            return
        }

        val residenceChanged =
            condominium != current.condominium ||
                    tower != current.tower ||
                    apartment != current.apartment

        _state.update {
            it.copy(
                isLoading = true,
                error = null
            )
        }

        saveJob?.cancel()

        saveJob = viewModelScope.launch {
            try {
                val userDocument = firestore
                    .collection("users")
                    .document(userId)

                if (auth.currentUser?.uid != userId) {
                    return@launch
                }

                // Mantener la estructura name/lastName
                // que utiliza el registro.
                val existingDocument = userDocument
                    .get()
                    .await()

                if (!existingDocument.exists()) {
                    throw IllegalStateException(
                        "No se encontró tu perfil."
                    )
                }

                if (auth.currentUser?.uid != userId) {
                    return@launch
                }

                val existingLastName = existingDocument
                    .getString("lastName")
                    .orEmpty()
                    .trim()

                val firstName: String
                val lastName: String

                if (
                    existingLastName.isNotBlank() &&
                    name.endsWith(
                        existingLastName,
                        ignoreCase = true
                    )
                ) {
                    firstName = name
                        .dropLast(existingLastName.length)
                        .trim()
                    lastName = existingLastName
                } else {
                    val parts = name.split(" ", limit = 2)
                    firstName = parts.first()
                    lastName = parts.getOrNull(1).orEmpty()
                }

                // Actualizar solamente datos de contacto.
                // No se modifican permisos ni verificación.
                userDocument.update(
                    mapOf(
                        "name" to firstName,
                        "lastName" to lastName,
                        "phone" to phone
                    )
                ).await()

                if (auth.currentUser?.uid != userId) {
                    return@launch
                }

                if (residenceChanged) {
                    // Solicitud pendiente de revisión.
                    // No modifica la residencia activa.
                    firestore
                        .collection("residence_change_requests")
                        .document(userId)
                        .set(
                            mapOf(
                                "userId" to userId,
                                "condominium" to condominium,
                                "tower" to tower,
                                "apartment" to apartment,
                                "status" to "pending",
                                "requestedAt" to
                                        FieldValue.serverTimestamp()
                            )
                        )
                        .await()
                }

                if (auth.currentUser?.uid != userId) {
                    return@launch
                }

                _state.update {
                    it.copy(
                        isLoading = false,
                        isEditing = false,
                        name = name,
                        initials = buildInitials(name),
                        phone = phone,
                        draftName = "",
                        draftPhone = "",
                        draftCondominium = "",
                        draftTower = "",
                        draftApartment = "",
                        nameError = null,
                        phoneError = null,
                        condominiumError = null,
                        towerError = null,
                        apartmentError = null,
                        successMessage =
                            if (residenceChanged) {
                                "Datos de contacto guardados. " +
                                        "Tu solicitud de cambio de " +
                                        "residencia quedó pendiente " +
                                        "de revisión."
                            } else {
                                "Los cambios de tu perfil " +
                                        "se guardaron."
                            }
                    )
                }

            } catch (exception: Exception) {
                if (auth.currentUser?.uid == userId) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error =
                                "No se pudieron guardar todos " +
                                        "los cambios. Verifica la conexión " +
                                        "y vuelve a cargar el perfil."
                        )
                    }
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
                draftCondominium = "",
                draftTower = "",
                draftApartment = "",
                nameError = null,
                phoneError = null,
                condominiumError = null,
                towerError = null,
                apartmentError = null
            )
        }
    }

    private fun validateResidenceField(
        value: String,
        field: String,
        maxLength: Int
    ): String? {
        return when {
            value.isBlank() ->
                "Ingresa el $field."

            value.length > maxLength ->
                "El campo $field es demasiado largo."

            else -> null
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

        val digitCount = phone.count {
            it in '0'..'9'
        }

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

    override fun onCleared() {
        auth.removeAuthStateListener(authListener)
        profileJob?.cancel()
        saveJob?.cancel()
        super.onCleared()
    }
}
