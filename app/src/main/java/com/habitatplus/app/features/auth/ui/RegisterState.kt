
package com.habitatplus.app.features.auth.ui

data class RegisterState(
    val name: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",
    val apartment: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val isRegistered: Boolean = false,
    val error: String? = null
)
