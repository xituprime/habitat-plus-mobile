package com.habitatplus.app.features.auth.ui

sealed interface SplashEvent {
    data object CheckSession : SplashEvent
}
