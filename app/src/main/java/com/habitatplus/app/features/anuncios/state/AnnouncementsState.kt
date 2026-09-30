package com.habitatplus.app.features.anuncios.state

import com.habitatplus.app.features.anuncios.model.Announcement
import com.habitatplus.app.features.anuncios.model.AnnouncementFilter

data class AnnouncementsState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val isOffline: Boolean = false,
    val query: String = "",
    val filter: AnnouncementFilter = AnnouncementFilter.ALL,
    val announcements: List<Announcement> = emptyList(),
    val visibleAnnouncements: List<Announcement> = emptyList()
)