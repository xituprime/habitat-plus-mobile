package com.habitatplus.app.features.perfil.intent

sealed interface ProfileIntent {

    data object Load : ProfileIntent

    data object ShowResidence : ProfileIntent

    data object DismissResidence : ProfileIntent
}