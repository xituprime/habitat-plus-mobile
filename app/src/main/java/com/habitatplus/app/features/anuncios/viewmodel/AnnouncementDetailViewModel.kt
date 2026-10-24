package com.habitatplus.app.features.anuncios.viewmodel

import androidx.lifecycle.ViewModel
import com.habitatplus.app.features.anuncios.data.AnnouncementsRepository
import com.habitatplus.app.features.anuncios.data.FakeAnnouncementsRepository
import com.habitatplus.app.features.anuncios.intent.AnnouncementDetailIntent
import com.habitatplus.app.features.anuncios.state.AnnouncementDetailState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AnnouncementDetailViewModel : ViewModel() {

    private val repository: AnnouncementsRepository =
        FakeAnnouncementsRepository

    private val _state = MutableStateFlow(AnnouncementDetailState())

    val state: StateFlow<AnnouncementDetailState> = _state.asStateFlow()

    fun onIntent(intent: AnnouncementDetailIntent) {
        when (intent) {
            is AnnouncementDetailIntent.Load -> {
                loadAnnouncement(intent.announcementId)
            }

            AnnouncementDetailIntent.Retry -> {
                loadAnnouncement(_state.value.announcementId)
            }

            AnnouncementDetailIntent.Back -> {
                _state.update {
                    it.copy(backRequested = true)
                }
            }

            AnnouncementDetailIntent.BackHandled -> {
                _state.update {
                    it.copy(backRequested = false)
                }
            }
        }
    }

    private fun loadAnnouncement(id: String?) {
        val normalizedId = id?.trim()?.takeIf { it.isNotEmpty() }

        _state.update {
            it.copy(
                announcementId = normalizedId,
                announcement = null,
                isLoading = true,
                error = null
            )
        }

        val announcement = normalizedId?.let {
            repository.getAnnouncementById(it)
        }

        _state.update {
            it.copy(
                announcement = announcement,
                isLoading = false,
                error = null,
                isOffline = false
            )
        }
    }
}