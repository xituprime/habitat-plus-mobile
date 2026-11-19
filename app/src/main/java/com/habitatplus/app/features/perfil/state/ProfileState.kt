
package com.habitatplus.app.features.perfil.state

data class ProfileState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val isOffline: Boolean = false,

    // Datos del usuario
    val name: String = "",
    val initials: String = "",
    val role: String = "",
    val condominium: String = "",
    val tower: String = "",
    val apartment: String = "",
    val email: String = "",
    val phone: String = "",
    val verified: Boolean = false,

    // Dialogo de residencia
    val showResidence: Boolean = false,

    // Estado de edicion
    val isEditing: Boolean = false,

    // Datos editables
    val draftName: String = "",
    val draftPhone: String = "",
    val draftCondominium: String = "",
    val draftTower: String = "",
    val draftApartment: String = "",

    // Errores de validacion
    val nameError: String? = null,
    val phoneError: String? = null,
    val condominiumError: String? = null,
    val towerError: String? = null,
    val apartmentError: String? = null,

    // Mensajes
    val successMessage: String? = null,

    // Navegacion
    val settingsRequested: Boolean = false
)
