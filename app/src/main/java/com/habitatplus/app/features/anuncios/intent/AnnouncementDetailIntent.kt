package com.habitatplus.app.features.anuncios.intent

sealed interface AnnouncementDetailIntent {

    data class Load(
        val announcementId: String?
    ) : AnnouncementDetailIntent

    data object Retry : AnnouncementDetailIntent

    data object Back : AnnouncementDetailIntent

    data object BackHandled : AnnouncementDetailIntent
}