package com.habitatplus.app.core.session

data class ResidentUser(
    val uid: String = "",
    val name: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",
    val apartment: String = "",
    val requestedApartment: String = "",
    val residenceStatus: String = "pending",
    val role: String = "resident",
    val photoUrl: String = ""
) {
    val fullName: String
        get() = listOf(name, lastName)
            .filter { it.isNotBlank() }
            .joinToString(" ")

    val initials: String
        get() = listOf(name, lastName)
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.first().uppercase() }
            .ifBlank { "H+" }
}
