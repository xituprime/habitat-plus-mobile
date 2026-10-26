package com.habitatplus.app.features.auth.ui

data class SplashState(
    val isLoading: Boolean = true,
    val destination: SplashDestination? = null,
    val error: String? = null
)

enum class SplashDestination {
    LOGIN,
    HOME
}
