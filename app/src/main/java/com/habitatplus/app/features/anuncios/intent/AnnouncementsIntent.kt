package com.habitatplus.app.features.anuncios.intent

import com.habitatplus.app.features.anuncios.model.AnnouncementFilter

sealed interface AnnouncementsIntent {

    data object Load : AnnouncementsIntent

    data object Refresh : AnnouncementsIntent

    data class Search(
        val query: String
    ) : AnnouncementsIntent

    data class SelectFilter(
        val filter: AnnouncementFilter
    ) : AnnouncementsIntent
}