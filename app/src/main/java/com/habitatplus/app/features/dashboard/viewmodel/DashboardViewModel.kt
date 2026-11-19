
package com.habitatplus.app.features.dashboard.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.habitatplus.app.data.repository.CloudReport
import com.habitatplus.app.data.repository.ReportFirestoreRepository
import com.habitatplus.app.features.dashboard.data.DashboardRepository
import com.habitatplus.app.features.dashboard.data.FakeDashboardRepository
import com.habitatplus.app.features.dashboard.intent.DashboardIntent
import com.habitatplus.app.features.dashboard.model.DashboardNavigation
import com.habitatplus.app.features.dashboard.model.DashboardReport
import com.habitatplus.app.features.dashboard.model.DashboardTab
import com.habitatplus.app.features.dashboard.state.DashboardState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DashboardViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val dashboardRepository: DashboardRepository =
        FakeDashboardRepository

    private val reportRepository = ReportFirestoreRepository()

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _state = MutableStateFlow(DashboardState())

    val state: StateFlow<DashboardState> =
        _state.asStateFlow()

    private var reportsJob: Job? = null
    private var profileJob: Job? = null
    private var observedUserId: String? = null
    private var profileUserId: String? = null

    private val authListener =
        FirebaseAuth.AuthStateListener { firebaseAuth ->
            val userId = firebaseAuth.currentUser?.uid

            if (userId != observedUserId ||
                userId != profileUserId
            ) {
                clearUserData()
                loadDashboard()
            }
        }

    init {
        auth.addAuthStateListener(authListener)
        loadDashboard()
    }

    fun onIntent(intent: DashboardIntent) {
        when (intent) {

            DashboardIntent.Load,
            DashboardIntent.Refresh -> loadDashboard()

            DashboardIntent.OpenAnnouncements -> {
                requestNavigation(
                    DashboardNavigation.Announcements
                )
            }

            is DashboardIntent.OpenAnnouncement -> {
                openAnnouncement(intent.id)
            }

            DashboardIntent.OpenParking -> {
                requestNavigation(
                    DashboardNavigation.Parking
                )
            }

            DashboardIntent.OpenAreas -> {
                requestNavigation(
                    DashboardNavigation.Areas
                )
            }

            DashboardIntent.OpenReports -> {
                requestNavigation(
                    DashboardNavigation.Reports
                )
            }

            DashboardIntent.OpenProfile -> {
                requestNavigation(
                    DashboardNavigation.Profile
                )
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
                    if (
                        current.pendingNavigation ==
                        intent.navigation
                    ) {
                        current.copy(
                            pendingNavigation = null
                        )
                    } else {
                        current
                    }
                }
            }
        }
    }

    private fun clearUserData() {
        reportsJob?.cancel()
        profileJob?.cancel()

        reportsJob = null
        profileJob = null

        observedUserId = null
        profileUserId = null

        _state.update {
            it.copy(
                userName = "",
                residence = "",
                reports = emptyList(),
                pendingNavigation = null
            )
        }
    }

    private fun loadDashboard() {
        val data = dashboardRepository.getDashboard()

        val hour = Calendar.getInstance()
            .get(Calendar.HOUR_OF_DAY)

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
                announcement = data.announcement,
                parkings = data.parkings,
                freeVisitorSpaces = data.freeVisitorSpaces,
                totalVisitorSpaces = data.totalVisitorSpaces,
                reservation = null,
                maintenance = data.maintenance,
                lastUpdated = updatedTime
            )
        }

        observeReports()
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val userId = auth.currentUser?.uid

        if (userId.isNullOrBlank()) {
            profileJob?.cancel()
            profileJob = null
            profileUserId = null

            _state.update {
                it.copy(
                    userName = "",
                    residence = ""
                )
            }
            return
        }

        if (
            profileUserId == userId &&
            profileJob?.isActive == true
        ) {
            return
        }

        profileJob?.cancel()
        profileUserId = userId

        _state.update {
            it.copy(
                userName = "",
                residence = ""
            )
        }

        profileJob = viewModelScope.launch {
            try {
                val document = firestore
                    .collection("users")
                    .document(userId)
                    .get()
                    .await()

                if (auth.currentUser?.uid != userId) {
                    return@launch
                }

                if (!document.exists()) {
                    _state.update {
                        it.copy(
                            userName = "Residente",
                            residence = "Residencia no registrada"
                        )
                    }
                    return@launch
                }

                val name = document
                    .getString("name")
                    .orEmpty()
                    .trim()

                val lastName = document
                    .getString("lastName")
                    .orEmpty()
                    .trim()

                val apartment = document
                    .getString("apartment")
                    .orEmpty()
                    .trim()

                val fullName = listOf(name, lastName)
                    .filter { it.isNotBlank() }
                    .joinToString(" ")
                    .ifBlank { "Residente" }

                val residenceText =
                    if (apartment.isNotBlank()) {
                        "Apartamento $apartment"
                    } else {
                        "Residencia no registrada"
                    }

                _state.update {
                    it.copy(
                        userName = fullName,
                        residence = residenceText
                    )
                }

            } catch (exception: Exception) {
                if (auth.currentUser?.uid == userId) {
                    _state.update {
                        it.copy(
                            userName = "Residente",
                            residence = "No se pudo cargar la residencia"
                        )
                    }
                }
            }
        }
    }

    private fun observeReports() {
        val userId = auth.currentUser?.uid

        if (userId.isNullOrBlank()) {
            reportsJob?.cancel()
            reportsJob = null
            observedUserId = null

            _state.update {
                it.copy(reports = emptyList())
            }
            return
        }

        if (
            observedUserId == userId &&
            reportsJob?.isActive == true
        ) {
            return
        }

        reportsJob?.cancel()
        observedUserId = userId

        _state.update {
            it.copy(reports = emptyList())
        }

        reportsJob = viewModelScope.launch {
            try {
                reportRepository
                    .observeMyReports()
                    .collect { entities ->

                        if (auth.currentUser?.uid != userId) {
                            return@collect
                        }

                        val dashboardReports = entities.map {
                            it.toDashboardReport()
                        }

                        _state.update {
                            it.copy(
                                reports = dashboardReports
                            )
                        }
                    }

            } catch (exception: Exception) {
                _state.update {
                    it.copy(
                        reports = emptyList(),
                        error =
                            "No se pudieron cargar los reportes."
                    )
                }
            }
        }
    }

    private fun CloudReport.toDashboardReport(): DashboardReport {
        val reportTitle = when (category) {
            "LIGHTING" -> "Reporte de iluminación"
            "NOISE" -> "Reporte de ruido"
            "WATER" -> "Reporte de agua"
            "OTHER" -> "Reporte general"
            else -> "Reporte de ${category.lowercase()}"
        }

        val reportStatus = when (status) {
            "PENDING" -> "Pendiente"
            "IN_PROGRESS" -> "En proceso"
            "RESOLVED" -> "Resuelto"
            else -> status
        }

        return DashboardReport(
            id = id,
            title = reportTitle,
            detail = description,
            status = reportStatus
        )
    }

    private fun openAnnouncement(id: String) {
        val current = _state.value

        if (current.isLoading) return

        if (
            id.isBlank() ||
            current.announcement?.id != id
        ) {
            _state.update {
                it.copy(
                    error =
                        "El anuncio seleccionado no está disponible."
                )
            }
            return
        }

        requestNavigation(
            DashboardNavigation.Announcement(id)
        )
    }

    private fun requestNavigation(
        navigation: DashboardNavigation
    ) {
        _state.update { current ->
            if (current.pendingNavigation == null) {
                current.copy(
                    pendingNavigation = navigation
                )
            } else {
                current
            }
        }
    }

    override fun onCleared() {
        auth.removeAuthStateListener(authListener)
        reportsJob?.cancel()
        profileJob?.cancel()
        super.onCleared()
    }
}
