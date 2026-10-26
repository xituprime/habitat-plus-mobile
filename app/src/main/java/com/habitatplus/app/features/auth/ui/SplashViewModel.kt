package com.habitatplus.app.features.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {

    private val _state = MutableStateFlow(SplashState())
    val state: StateFlow<SplashState> = _state.asStateFlow()

    fun onEvent(event: SplashEvent) {
        when (event) {
            SplashEvent.CheckSession -> checkSession()
        }
    }

    private fun checkSession() {
        if (_state.value.destination != null) return

        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true, error = null)
            }

            delay(1000)

            // Temporal: sin sesiones persistentes todavía.
            _state.update {
                it.copy(
                    isLoading = false,
                    destination = SplashDestination.LOGIN
                )
            }
        }
    }
}
