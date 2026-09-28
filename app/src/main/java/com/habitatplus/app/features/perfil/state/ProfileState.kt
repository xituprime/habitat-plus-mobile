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
    val showResidence: Boolean = false
)