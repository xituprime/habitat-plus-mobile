package com.habitatplus.app.features.dashboard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitatplus.app.core.data.FakeResidentRepository
import com.habitatplus.app.core.data.ResidentRepository
import com.habitatplus.app.features.dashboard.data.DashboardRepository
import com.habitatplus.app.features.dashboard.data.FakeDashboardRepository
import com.habitatplus.app.features.dashboard.intent.DashboardIntent
import com.habitatplus.app.features.dashboard.model.DashboardNavigation
import com.habitatplus.app.features.dashboard.model.DashboardTab
import com.habitatplus.app.features.dashboard.state.DashboardState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import com.habitatplus.app.data.remote.DemoApiService

class DashboardViewModel : ViewModel() {

    private val dashboardRepository: DashboardRepository =
        FakeDashboardRepository

    private val residentRepository: ResidentRepository =
        FakeResidentRepository

    private val demoApiService = DemoApiService()

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    init {
        loadDashboard()

        viewModelScope.launch {
            residentRepository.resident.collect { resident ->
                _state.update { current ->
                    current.copy(
                        userName = resident.name,
                        residence = "${resident.condominium} · " +
                                resident.apartment
                    )
                }
            }
        }
    }

    fun onIntent(intent: DashboardIntent) {
        when (intent) {
            DashboardIntent.LoadRemoteData -> {
                loadRemoteData()
            }
            DashboardIntent.Load,
            DashboardIntent.Refresh -> loadDashboard()

            DashboardIntent.OpenAnnouncements -> {
                requestNavigation(DashboardNavigation.Announcements)
            }

            is DashboardIntent.OpenAnnouncement -> {
                openAnnouncement(intent.id)
            }

            DashboardIntent.OpenParking -> {
                requestNavigation(DashboardNavigation.Parking)
            }

            DashboardIntent.OpenAreas -> {
                requestNavigation(DashboardNavigation.Areas)
            }

            DashboardIntent.OpenReports -> {
                requestNavigation(DashboardNavigation.Reports)
            }

            DashboardIntent.OpenProfile -> {
                requestNavigation(DashboardNavigation.Profile)
            }

            is DashboardIntent.SelectTab -> {
                if (intent.tab != DashboardTab.HOME) {
                    requestNavigation(
                        DashboardNavigation.Tab(intent.tab)
                    )
                }
            }

            is DashboardIntent.NavigationHandled -> {
                _state.update { current ->
                    if (current.pendingNavigation == intent.navigation) {
                        current.copy(pendingNavigation = null)
                    } else {
                        current
                    }
                }
            }
        }
    }

    private fun loadDashboard() {
        val data = dashboardRepository.getDashboard()
        val resident = residentRepository.resident.value

        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        val greeting = when (hour) {
            in 5..11 -> "Buenos días"
            in 12..18 -> "Buenas tardes"
            else -> "Buenas noches"
        }

        val updatedTime = SimpleDateFormat(
            "HH:mm:ss",
            Locale.getDefault()
        ).format(Date())

        _state.update { current ->
            current.copy(
                isLoading = false,
                error = null,
                isOffline = false,
                greeting = greeting,
                userName = resident.name,
                residence = "${resident.condominium} · ${resident.apartment}",
                announcement = data.announcement,
                parkings = data.parkings,
                freeVisitorSpaces = data.freeVisitorSpaces,
                totalVisitorSpaces = data.totalVisitorSpaces,
                reservation = data.reservation,
                reports = data.reports,
                maintenance = data.maintenance,
                lastUpdated = updatedTime
            )
        }
    }

    private fun openAnnouncement(id: String) {
        val current = _state.value

        if (current.isLoading) return

        if (id.isBlank() || current.announcement?.id != id) {
            _state.update {
                it.copy(
                    error = "El anuncio seleccionado no está disponible."
                )
            }
            return
        }

        requestNavigation(DashboardNavigation.Announcement(id))
    }

    private fun requestNavigation(navigation: DashboardNavigation) {
        _state.update { current ->
            if (current.pendingNavigation == null) {
                current.copy(pendingNavigation = navigation)
            } else {
                current
            }
        }
    }


    private fun loadRemoteData() {
        if (_state.value.isHttpLoading) return

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isHttpLoading = true,
                    httpResult = null,
                    httpError = null
                )
            }

            try {
                val response = demoApiService.fetchPost()

                _state.update {
                    it.copy(
                        isHttpLoading = false,
                        httpResult = response,
                        httpError = null
                    )
                }
            } catch (exception: Exception) {
                _state.update {
                    it.copy(
                        isHttpLoading = false,
                        httpResult = null,
                        httpError = exception.message
                            ?: "No se pudo conectar a internet."
                    )
                }
            }
        }
    }

}
