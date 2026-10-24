package com.habitatplus.app.features.anuncios.state

import com.habitatplus.app.features.anuncios.model.Announcement

data class AnnouncementDetailState(
    val announcementId: String? = null,
    val announcement: Announcement? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val isOffline: Boolean = false,
    val backRequested: Boolean = false
)