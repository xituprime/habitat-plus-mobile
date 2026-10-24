package com.habitatplus.app.features.anuncios.viewmodel

import androidx.lifecycle.ViewModel
import com.habitatplus.app.features.anuncios.data.AnnouncementsRepository
import com.habitatplus.app.features.anuncios.data.FakeAnnouncementsRepository
import com.habitatplus.app.features.anuncios.intent.AnnouncementsIntent
import com.habitatplus.app.features.anuncios.model.AnnouncementFilter
import com.habitatplus.app.features.anuncios.state.AnnouncementsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.Normalizer
import java.util.Locale

class AnnouncementsViewModel : ViewModel() {

    private val repository: AnnouncementsRepository =
        FakeAnnouncementsRepository

    private val _state = MutableStateFlow(AnnouncementsState())
    val state: StateFlow<AnnouncementsState> = _state.asStateFlow()

    init {
        loadAnnouncements()
    }

    fun onIntent(intent: AnnouncementsIntent) {
        when (intent) {
            AnnouncementsIntent.Load,
            AnnouncementsIntent.Refresh -> loadAnnouncements()

            is AnnouncementsIntent.Search -> {
                _state.update { current ->
                    applyFilters(
                        current.copy(query = intent.query)
                    )
                }
            }

            is AnnouncementsIntent.SelectFilter -> {
                _state.update { current ->
                    applyFilters(
                        current.copy(filter = intent.filter)
                    )
                }
            }
        }
    }

    private fun loadAnnouncements() {
        val announcements = repository.getAnnouncements()

        _state.update { current ->
            applyFilters(
                current.copy(
                    isLoading = false,
                    error = null,
                    isOffline = false,
                    announcements = announcements
                )
            )
        }
    }

    private fun applyFilters(
        current: AnnouncementsState
    ): AnnouncementsState {
        val query = normalize(current.query.trim())

        val visible = current.announcements.filter { announcement ->
            val matchesCategory = when (current.filter) {
                AnnouncementFilter.ALL -> true

                AnnouncementFilter.PRIORITY ->
                    announcement.priority

                AnnouncementFilter.MAINTENANCE ->
                    announcement.category == "Mantenimiento"

                AnnouncementFilter.INSTITUTIONAL ->
                    announcement.category == "Institucional"

                AnnouncementFilter.COMMUNITY ->
                    announcement.category == "Convivencia"
            }

            val searchable = normalize(
                "${announcement.title} " +
                        "${announcement.summary} " +
                        announcement.category
            )

            matchesCategory &&
                    (query.isBlank() || query in searchable)
        }

        return current.copy(visibleAnnouncements = visible)
    }

    private fun normalize(value: String): String {
        return Normalizer.normalize(value, Normalizer.Form.NFD)
            .replace(Regex("\\p{M}+"), "")
            .lowercase(Locale.ROOT)
    }
}