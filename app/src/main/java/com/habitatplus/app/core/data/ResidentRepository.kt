package com.habitatplus.app.core.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class Resident(
    val name: String,
    val role: String,
    val condominium: String,
    val tower: String,
    val apartment: String,
    val email: String,
    val phone: String,
    val verified: Boolean
)

interface ResidentRepository {
    val resident: StateFlow<Resident>

    fun updateContact(name: String, phone: String)
}

/**
 * Datos ficticios compartidos durante la ejecución de la app.
 * No guarda información en disco ni consulta un servidor.
 */
object FakeResidentRepository : ResidentRepository {

    private val _resident = MutableStateFlow(
        Resident(
            name = "Kenett Ortega",
            role = "Residente",
            condominium = "Condominio Las Acacias",
            tower = "Torre A",
            apartment = "A-203",
            email = "kenett@example.com",
            phone = "+502 5555-0100",
            verified = true
        )
    )

    override val resident: StateFlow<Resident> = _resident.asStateFlow()

    override fun updateContact(name: String, phone: String) {
        _resident.update { current ->
            current.copy(
                name = name,
                phone = phone
            )
        }
    }
}