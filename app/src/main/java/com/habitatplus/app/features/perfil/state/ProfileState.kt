package com.habitatplus.app.features.perfil.state

data class ProfileState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val isOffline: Boolean = false,

    val name: String = "",
    val initials: String = "",
    val role: String = "",
    val condominium: String = "",
    val tower: String = "",
    val apartment: String = "",
    val email: String = "",
    val phone: String = "",
    val verified: Boolean = false,

    val showResidence: Boolean = false,

    val isEditing: Boolean = false,
    val draftName: String = "",
    val draftPhone: String = "",
    val nameError: String? = null,
    val phoneError: String? = null,
    val successMessage: String? = null,

    val settingsRequested: Boolean = false
)