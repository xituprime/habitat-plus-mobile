
package com.habitatplus.app.features.reportes.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.habitatplus.app.data.local.DatabaseProvider
import com.habitatplus.app.data.local.ReportEntity
import com.habitatplus.app.data.repository.ReportRepository
import com.habitatplus.app.features.notificaciones.data.NotificationRepository
import com.habitatplus.app.features.notificaciones.model.NotificationType
import com.habitatplus.app.features.reportes.intent.ReportIntent
import com.habitatplus.app.features.reportes.model.Report
import com.habitatplus.app.features.reportes.model.ReportCategory
import com.habitatplus.app.features.reportes.model.ReportStatus
import com.habitatplus.app.features.reportes.state.ReportState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class ReportViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = ReportRepository(
        DatabaseProvider
            .getDatabase(application)
            .reportDao()
    )

    private val notificationRepository =
        NotificationRepository(application)

    private val auth = FirebaseAuth.getInstance()

    private val _state = MutableStateFlow(
        ReportState(isLoading = true)
    )

    val state: StateFlow<ReportState> =
        _state.asStateFlow()

    private var reportsJob: Job? = null
    private var observedUserId: String? = null

    init {
        loadReports()
    }

    fun onIntent(intent: ReportIntent) {
        when (intent) {

            ReportIntent.LoadReports -> {
                loadReports()
            }

            is ReportIntent.SearchReports -> {
                _state.update {
                    it.copy(searchQuery = intent.query)
                }
            }

            is ReportIntent.FilterByStatus -> {
                _state.update {
                    it.copy(selectedStatus = intent.status)
                }
            }

            is ReportIntent.SelectCategory -> {
                _state.update {
                    it.copy(selectedCategory = intent.category)
                }
            }

            is ReportIntent.UpdateDescription -> {
                if (intent.description.length <= 500) {
                    _state.update {
                        it.copy(description = intent.description)
                    }
                }
            }

            is ReportIntent.SelectReport -> {
                selectReport(intent.reportId)
            }

            is ReportIntent.SelectPhoto -> {
                _state.update {
                    it.copy(
                        selectedPhotoUri = intent.photoUri,
                        hasSelectedPhoto =
                            !intent.photoUri.isNullOrBlank()
                    )
                }
            }

            ReportIntent.SubmitReport -> {
                submitReport()
            }

            ReportIntent.ClearReportSubmission -> {
                _state.update {
                    it.copy(reportSubmitted = false)
                }
            }
        }
    }

    private fun loadReports() {
        val userId = auth.currentUser?.uid

        if (userId.isNullOrBlank()) {
            reportsJob?.cancel()
            reportsJob = null
            observedUserId = null

            _state.update {
                it.copy(
                    reports = emptyList(),
                    selectedReportId = null,
                    isLoading = false,
                    errorMessage =
                        "Debes iniciar sesión para consultar reportes."
                )
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
            it.copy(
                reports = emptyList(),
                selectedReportId = null,
                isLoading = true,
                errorMessage = null
            )
        }

        reportsJob = viewModelScope.launch {
            try {
                repository.getReportsByUser(userId)
                    .collect { entities ->

                        if (auth.currentUser?.uid != userId) {
                            _state.update {
                                it.copy(
                                    reports = emptyList(),
                                    selectedReportId = null,
                                    isLoading = false
                                )
                            }
                            return@collect
                        }

                        val reports = entities.map {
                            it.toReport()
                        }

                        _state.update { current ->
                            current.copy(
                                reports = reports,
                                isLoading = false,
                                errorMessage = null
                            )
                        }
                    }

            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                if (auth.currentUser?.uid == userId) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage =
                                "No se pudieron cargar los reportes."
                        )
                    }
                }
            }
        }
    }

    private fun selectReport(reportId: String) {
        val exists = _state.value.reports.any {
            it.id == reportId
        }

        _state.update {
            it.copy(
                selectedReportId =
                    if (exists) reportId else null,
                errorMessage =
                    if (exists) {
                        null
                    } else {
                        "No se encontró el reporte seleccionado."
                    }
            )
        }
    }

    private fun submitReport() {
        val current = _state.value

        if (current.isSubmitting || current.isLoading) {
            return
        }

        val userId = auth.currentUser?.uid

        if (userId.isNullOrBlank()) {
            _state.update {
                it.copy(
                    errorMessage =
                        "Debes iniciar sesión para crear un reporte."
                )
            }
            return
        }

        val category = current.selectedCategory ?: return
        val description = current.description.trim()

        if (
            description.isBlank() ||
            description.length > 500
        ) {
            return
        }

        _state.update {
            it.copy(
                isSubmitting = true,
                reportSubmitted = false,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            try {
                val now = Date()

                val date = SimpleDateFormat(
                    "dd MMM, yyyy",
                    Locale("es", "GT")
                ).format(now)

                val timestamp = SimpleDateFormat(
                    "dd MMM, yyyy • hh:mm a",
                    Locale("es", "GT")
                ).format(now)

                val reportId = "REP-" +
                        UUID.randomUUID().toString()
                            .substring(0, 8)
                            .uppercase(Locale.US)

                val newReport = Report(
                    id = reportId,
                    category = category,
                    description = description,
                    location = "Ubicación no especificada",
                    date = date,
                    lastUpdate = timestamp,
                    status = ReportStatus.PENDING,
                    hasPhoto =
                        !current.selectedPhotoUri.isNullOrBlank(),
                    photoUri = current.selectedPhotoUri
                )

                if (auth.currentUser?.uid != userId) {
                    throw IllegalStateException(
                        "La sesión cambió durante el guardado."
                    )
                }

                repository.saveReport(
                    newReport.toEntity(userId)
                )

                _state.update {
                    it.copy(
                        selectedCategory = null,
                        description = "",
                        hasSelectedPhoto = false,
                        selectedPhotoUri = null,
                        isSubmitting = false,
                        reportSubmitted = true,
                        selectedReportId = reportId,
                        errorMessage = null
                    )
                }

                try {
                    if (auth.currentUser?.uid == userId) {
                        val categoryName = when (category) {
                            ReportCategory.LIGHTING ->
                                "iluminación"
                            ReportCategory.NOISE ->
                                "ruido"
                            ReportCategory.WATER ->
                                "agua"
                            else ->
                                "mantenimiento"
                        }

                        notificationRepository.createNotification(
                            userId = userId,
                            type = NotificationType.REPORT,
                            title = "Reporte registrado",
                            message =
                                "Tu reporte de $categoryName " +
                                        "fue registrado correctamente. " +
                                        "Código: $reportId.",
                            relatedId = reportId
                        )
                    }
                } catch (exception: CancellationException) {
                    throw exception
                } catch (exception: Exception) {
                    android.util.Log.w(
                        "ReportViewModel",
                        "No se pudo guardar la notificación.",
                        exception
                    )
                }

            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                if (auth.currentUser?.uid == userId) {
                    _state.update {
                        it.copy(
                            isSubmitting = false,
                            reportSubmitted = false,
                            errorMessage =
                                "No se pudo guardar el reporte."
                        )
                    }
                }
            }
        }
    }

    private fun Report.toEntity(
        userId: String
    ): ReportEntity {
        return ReportEntity(
            id = id,
            category = category.name,
            description = description,
            location = location,
            date = date,
            lastUpdate = lastUpdate,
            status = status.name,
            hasPhoto = hasPhoto,
            photoUri = photoUri,
            userId = userId
        )
    }

    private fun ReportEntity.toReport(): Report {
        return Report(
            id = id,
            category = ReportCategory.entries
                .firstOrNull {
                    it.name == category
                } ?: ReportCategory.OTHER,
            description = description,
            location = location,
            date = date,
            lastUpdate = lastUpdate,
            status = ReportStatus.entries
                .firstOrNull {
                    it.name == status
                } ?: ReportStatus.PENDING,
            hasPhoto = hasPhoto,
            photoUri = photoUri
        )
    }

    override fun onCleared() {
        reportsJob?.cancel()
        super.onCleared()
    }
}
