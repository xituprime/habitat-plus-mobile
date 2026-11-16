
package com.habitatplus.app.features.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _state = MutableStateFlow(SplashState())
    val state: StateFlow<SplashState> = _state.asStateFlow()

    private var checkingSession = false

    fun onEvent(event: SplashEvent) {
        when (event) {
            SplashEvent.CheckSession -> checkSession()
        }
    }

    private fun checkSession() {
        if (checkingSession || _state.value.destination != null) {
            return
        }

        checkingSession = true

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }

            delay(700)

            val destination =
                if (auth.currentUser != null) {
                    SplashDestination.HOME
                } else {
                    SplashDestination.LOGIN
                }

            _state.update {
                it.copy(
                    isLoading = false,
                    destination = destination,
                    error = null
                )
            }

            checkingSession = false
        }
    }
}
