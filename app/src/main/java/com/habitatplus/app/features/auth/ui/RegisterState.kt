
package com.habitatplus.app.features.auth.ui

data class RegisterState(
    val name: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",

    // Datos de residencia
    val condominium: String = "",
    val tower: String = "",
    val apartment: String = "",

    // Seguridad de la cuenta
    val password: String = "",
    val confirmPassword: String = "",

    // Estado del registro
    val isLoading: Boolean = false,
    val isRegistered: Boolean = false,
    val error: String? = null
)
