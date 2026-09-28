package com.habitatplus.app.features.perfil.viewmodel

import androidx.lifecycle.ViewModel
import com.habitatplus.app.features.perfil.intent.ProfileIntent
import com.habitatplus.app.features.perfil.state.ProfileState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProfileViewModel : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())

    val state = _state.asStateFlow()

    init {
        onIntent(ProfileIntent.Load)
    }

    fun onIntent(intent: ProfileIntent) {
        when (intent) {
            ProfileIntent.Load -> loadDemo()

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
        }
    }

    private fun loadDemo() {
        // Datos ficticios. Pendiente conectar el usuario autenticado.
        _state.value = ProfileState(
            isLoading = false,
            name = "Kenett Ortega",
            initials = "KO",
            role = "Residente",
            condominium = "Condominio Las Acacias",
            tower = "Torre A",
            apartment = "A-203",
            email = "kenett@example.com",
            phone = "+502 5555-0100",
            verified = true
        )
    }
}