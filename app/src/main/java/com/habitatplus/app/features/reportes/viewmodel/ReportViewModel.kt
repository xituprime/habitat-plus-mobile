package com.habitatplus.app.features.reportes.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.habitatplus.app.data.local.DatabaseProvider
import com.habitatplus.app.data.local.ReportEntity
import com.habitatplus.app.data.repository.ReportRepository
import com.habitatplus.app.features.reportes.intent.ReportIntent
import com.habitatplus.app.features.reportes.model.Report
import com.habitatplus.app.features.reportes.model.ReportCategory
import com.habitatplus.app.features.reportes.model.ReportStatus
import com.habitatplus.app.features.reportes.state.ReportState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = ReportRepository(
        DatabaseProvider
            .getDatabase(application)
            .reportDao()
    )

    private val initialReports = listOf(
        Report(
            id = "REP-2041",
            category = ReportCategory.LIGHTING,
            description = "La luz del pasillo principal parpadea continuamente desde ayer por la tarde, dificultando el tránsito de noche.",
            location = "Torre A - Piso 3",
            date = "14 Oct, 2024",
            lastUpdate = "14 Oct, 2024 • 11:30 AM",
            status = ReportStatus.PENDING,
            hasPhoto = true
        ),
        Report(
            id = "REP-2038",
            category = ReportCategory.NOISE,
            description = "El motor de extracción emite una vibración metálica constante perceptible en los apartamentos de planta baja.",
            location = "Subsuelo 1",
            date = "12 Oct, 2024",
            lastUpdate = "13 Oct, 2024 • 09:20 AM",
            status = ReportStatus.IN_PROGRESS
        ),
        Report(
            id = "REP-2015",
            category = ReportCategory.WATER,
            description = "Se completó el cambio de empaque y calibración de válvula. Área verificada sin goteo residual.",
            location = "Áreas Comunes",
            date = "08 Oct, 2024",
            lastUpdate = "09 Oct, 2024 • 04:10 PM",
            status = ReportStatus.RESOLVED,
            hasPhoto = true
        )
    )

    private val _state = MutableStateFlow(
        ReportState(
            isLoading = true
        )
    )

    val state: StateFlow<ReportState> =
        _state.asStateFlow()

    init {
        initializeReports()
    }

    fun onIntent(intent: ReportIntent) {
        when (intent) {

            ReportIntent.LoadReports -> {
                loadReports()
            }

            is ReportIntent.SearchReports -> {
                _state.update {
                    it.copy(
                        searchQuery = intent.query
                    )
                }
            }

            is ReportIntent.FilterByStatus -> {
                _state.update {
                    it.copy(
                        selectedStatus = intent.status
                    )
                }
            }

            is ReportIntent.SelectCategory -> {
                _state.update {
                    it.copy(
                        selectedCategory = intent.category
                    )
                }
            }

            is ReportIntent.UpdateDescription -> {
                if (intent.description.length <= 500) {
                    _state.update {
                        it.copy(
                            description = intent.description
                        )
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
                    it.copy(
                        reportSubmitted = false
                    )
                }
            }
        }
    }

    // Inicializar Room y cargar los reportes.
    // Los ejemplos solo se insertan si no existen.

    private fun initializeReports() {
        viewModelScope.launch {
            try {
                val savedReports = repository.reports.first()

                val savedIds = savedReports
                    .map { it.id }
                    .toSet()

                initialReports.forEach { report ->
                    if (report.id !in savedIds) {
                        repository.saveReport(
                            report.toEntity()
                        )
                    }
                }

                // Observar cambios posteriores de Room.
                repository.reports.collect { entities ->

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

            } catch (exception: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage =
                            "No se pudieron cargar los reportes locales."
                    )
                }
            }
        }
    }

    private fun loadReports() {
        // La colección de Room mantiene la lista
        // actualizada automáticamente.
        // No reiniciamos los reportes ni el formulario.
        _state.update {
            it.copy(
                isLoading = false
            )
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

        val category = current.selectedCategory ?: return
        val description = current.description.trim()

        if (
            description.isBlank() ||
            description.length > 500
        ) {
            return
        }

        // Bloquear temporalmente el botón para
        // impedir envíos repetidos.

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

                // Consultar Room para evitar reutilizar
                // un ID después de reiniciar la app.

                val savedReports =
                    repository.reports.first()

                val highestNumber = savedReports
                    .mapNotNull { entity ->
                        entity.id
                            .removePrefix("REP-")
                            .toIntOrNull()
                    }
                    .maxOrNull() ?: 2041

                val nextNumber = maxOf(
                    highestNumber + 1,
                    2042
                )

                val newReport = Report(
                    id = "REP-$nextNumber",
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

                // Guardar en Room antes de mostrar éxito.

                repository.saveReport(
                    newReport.toEntity()
                )

                // Actualizar inmediatamente el estado
                // para que la navegación encuentre el
                // reporte recién creado.

                _state.update {
                    it.copy(
                        reports = listOf(newReport) +
                                it.reports.filterNot { existing ->
                                    existing.id == newReport.id
                                },
                        selectedCategory = null,
                        description = "",
                        hasSelectedPhoto = false,
                        selectedPhotoUri = null,
                        isSubmitting = false,
                        reportSubmitted = true,
                        selectedReportId = newReport.id,
                        errorMessage = null
                    )
                }

            } catch (exception: Exception) {

                _state.update {
                    it.copy(
                        isSubmitting = false,
                        reportSubmitted = false,
                        errorMessage =
                            "No se pudo guardar el reporte en Room."
                    )
                }
            }
        }
    }

    // Convertir el modelo de la aplicación
    // al modelo de almacenamiento Room.

    private fun Report.toEntity(): ReportEntity {
        return ReportEntity(
            id = id,
            category = category.name,
            description = description,
            location = location,
            date = date,
            lastUpdate = lastUpdate,
            status = status.name,
            hasPhoto = hasPhoto,
            photoUri = photoUri
        )
    }

    // Convertir un registro Room al modelo
    // que utilizan nuestras pantallas.

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
}
