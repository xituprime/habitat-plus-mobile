
package com.habitatplus.app.features.reportes.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.habitatplus.app.data.repository.CloudReport
import com.habitatplus.app.data.repository.ReportFirestoreRepository
import com.habitatplus.app.features.notificaciones.data.NotificationRepository
import com.habitatplus.app.features.notificaciones.model.NotificationType
import com.habitatplus.app.features.reportes.intent.ReportIntent
import com.habitatplus.app.features.reportes.model.Report
import com.habitatplus.app.features.reportes.model.ReportCategory
import com.habitatplus.app.features.reportes.model.ReportStatus
import com.habitatplus.app.features.reportes.state.ReportState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val auth = FirebaseAuth.getInstance()
    private val repository = ReportFirestoreRepository()

    private val notificationRepository =
        NotificationRepository(application)

    private val _state = MutableStateFlow(
        ReportState(isLoading = true)
    )

    val state: StateFlow<ReportState> =
        _state.asStateFlow()

    private var observerJob: Job? = null
    private var observedUid: String? = null
    private var sessionVersion = 0

    init {
        loadReports()
    }

    fun onIntent(intent: ReportIntent) {
        when (intent) {

            ReportIntent.LoadReports -> loadReports()

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

            is ReportIntent.UpdateLocation -> {
                if (intent.location.length <= 150) {
                    _state.update {
                        it.copy(location = intent.location)
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

            ReportIntent.SubmitReport -> submitReport()

            ReportIntent.ClearReportSubmission -> {
                _state.update {
                    it.copy(reportSubmitted = false)
                }
            }
        }
    }

    fun clearSession() {
        sessionVersion++
        observerJob?.cancel()
        observerJob = null
        observedUid = null

        _state.value = ReportState(
            isLoading = false
        )
    }

    private fun sameSession(
        uid: String,
        version: Int
    ): Boolean {
        return auth.currentUser?.uid == uid &&
                observedUid == uid &&
                sessionVersion == version
    }

    private fun photoFile(
        uid: String,
        reportId: String
    ): File {
        val directory = File(
            getApplication<Application>().filesDir,
            "report_photos/$uid"
        )

        return File(directory, "$reportId.jpg")
    }

    private suspend fun savePhoto(
        uid: String,
        reportId: String,
        uriString: String
    ): String = withContext(Dispatchers.IO) {

        val context = getApplication<Application>()
        val source = Uri.parse(uriString)
        val destination = photoFile(uid, reportId)

        destination.parentFile?.mkdirs()

        try {
            context.contentResolver.openInputStream(source)
                ?.use { input ->
                    destination.outputStream().use { output ->
                        input.copyTo(output)
                    }
                } ?: error("No se pudo abrir la fotografía.")

            require(
                destination.exists() &&
                        destination.length() > 0
            ) {
                "La fotografía está vacía."
            }

            destination.toURI().toString()

        } catch (e: Exception) {
            destination.delete()
            throw e
        }
    }

    private fun existingPhoto(
        uid: String,
        reportId: String
    ): String? {
        val file = photoFile(uid, reportId)

        return if (file.exists() && file.length() > 0) {
            file.toURI().toString()
        } else {
            null
        }
    }

    private fun loadReports() {
        val uid = auth.currentUser?.uid

        if (uid == null) {
            clearSession()

            _state.update {
                it.copy(
                    errorMessage = "Debes iniciar sesión."
                )
            }
            return
        }

        if (
            observedUid == uid &&
            observerJob?.isActive == true
        ) return

        observerJob?.cancel()

        sessionVersion++
        val version = sessionVersion
        observedUid = uid

        _state.value = ReportState(isLoading = true)

        observerJob = viewModelScope.launch {
            try {
                repository.observeMyReports()
                    .collect { cloudReports ->

                        if (!sameSession(uid, version)) {
                            return@collect
                        }

                        val reports = cloudReports.map {
                            it.toReport(uid)
                        }

                        _state.update { current ->
                            current.copy(
                                reports = reports,
                                isLoading = false,
                                errorMessage = null,
                                selectedReportId =
                                    current.selectedReportId?.takeIf { id ->
                                        reports.any { it.id == id }
                                    }
                            )
                        }
                    }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(
                    "HabitatReports",
                    "Error al cargar reportes",
                    e
                )

                if (sameSession(uid, version)) {
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
                    if (exists) null
                    else "No se encontró el reporte."
            )
        }
    }

    private fun submitReport() {
        val current = _state.value

        if (current.isSubmitting || current.isLoading) {
            return
        }

        val uid = auth.currentUser?.uid

        if (uid == null) {
            _state.update {
                it.copy(
                    errorMessage = "Debes iniciar sesión."
                )
            }
            return
        }

        val category = current.selectedCategory
        val description = current.description.trim()
        val location = current.location.trim()

        if (category == null) {
            _state.update {
                it.copy(
                    errorMessage = "Selecciona una categoría."
                )
            }
            return
        }

        if (description.isBlank() || description.length > 500) {
            _state.update {
                it.copy(
                    errorMessage =
                        "Ingresa una descripción válida."
                )
            }
            return
        }

        if (location.isBlank() || location.length > 150) {
            _state.update {
                it.copy(
                    errorMessage =
                        "Ingresa la ubicación del incidente."
                )
            }
            return
        }

        val version = sessionVersion

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
                val locale = Locale.forLanguageTag("es-GT")

                val date = SimpleDateFormat(
                    "yyyy-MM-dd",
                    locale
                ).format(now)

                val timestamp = SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    locale
                ).format(now)

                if (!sameSession(uid, version)) {
                    return@launch
                }

                // Guardar los datos del reporte en Firestore.
                val cloudReport = CloudReport(
                    category = category.name,
                    description = description,
                    location = location,
                    date = date,
                    lastUpdate = timestamp,
                    status = ReportStatus.PENDING.name,
                    hasPhoto = false,
                    photoUrl = null
                )

                val reportId = repository.createReport(
                    cloudReport
                )

                if (!sameSession(uid, version)) {
                    return@launch
                }

                // La fotografía se guarda localmente.
                // Un fallo de imagen no invalida el reporte.
                var savedPhotoUri: String? = null
                var photoWarning: String? = null

                current.selectedPhotoUri?.let { uri ->
                    try {
                        savedPhotoUri = savePhoto(
                            uid = uid,
                            reportId = reportId,
                            uriString = uri
                        )
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        Log.e(
                            "HabitatReports",
                            "Error al guardar fotografía local",
                            e
                        )

                        photoWarning =
                            "El reporte se guardó, pero no " +
                                    "se pudo conservar la fotografía."
                    }
                }

                if (!sameSession(uid, version)) {
                    return@launch
                }

                val newReport = Report(
                    id = reportId,
                    category = category,
                    description = description,
                    location = location,
                    date = date,
                    lastUpdate = timestamp,
                    status = ReportStatus.PENDING,
                    hasPhoto = savedPhotoUri != null,
                    photoUri = savedPhotoUri
                )

                _state.update {
                    it.copy(
                        reports = listOf(newReport) +
                                it.reports.filterNot { report ->
                                    report.id == reportId
                                },
                        selectedCategory = null,
                        description = "",
                        location = "",
                        hasSelectedPhoto = false,
                        selectedPhotoUri = null,
                        isSubmitting = false,
                        reportSubmitted = true,
                        selectedReportId = reportId,
                        errorMessage = photoWarning
                    )
                }

                try {
                    notificationRepository.createNotification(
                        userId = uid,
                        type = NotificationType.REPORT,
                        title = "Reporte registrado",
                        message =
                            "Tu reporte fue registrado. " +
                                    "Código: $reportId.",
                        relatedId = reportId
                    )
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.w(
                        "HabitatReports",
                        "Error de notificación",
                        e
                    )
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(
                    "HabitatReports",
                    "Error al crear reporte",
                    e
                )

                if (sameSession(uid, version)) {
                    _state.update {
                        it.copy(
                            isSubmitting = false,
                            reportSubmitted = false,
                            errorMessage =
                                "No se pudo guardar el reporte: " +
                                        (e.localizedMessage
                                            ?: "Error desconocido.")
                        )
                    }
                }
            }
        }
    }

    private fun CloudReport.toReport(
        uid: String
    ): Report {
        val photo = existingPhoto(uid, id)

        return Report(
            id = id,
            category = ReportCategory.entries.firstOrNull {
                it.name == category
            } ?: ReportCategory.OTHER,
            description = description,
            location = location,
            date = date,
            lastUpdate = lastUpdate,
            status = ReportStatus.entries.firstOrNull {
                it.name == status
            } ?: ReportStatus.PENDING,
            hasPhoto = photo != null,
            photoUri = photo
        )
    }

    override fun onCleared() {
        observerJob?.cancel()
        super.onCleared()
    }
}