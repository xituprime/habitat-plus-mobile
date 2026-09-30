package com.habitatplus.app.features.reportes.viewmodel

import androidx.lifecycle.ViewModel
import com.habitatplus.app.features.reportes.intent.ReportIntent
import com.habitatplus.app.features.reportes.model.Report
import com.habitatplus.app.features.reportes.model.ReportCategory
import com.habitatplus.app.features.reportes.model.ReportStatus
import com.habitatplus.app.features.reportes.state.ReportState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ReportViewModel : ViewModel() {

    private val _state = MutableStateFlow(
        ReportState()
    )

    val state: StateFlow<ReportState> =
        _state.asStateFlow()

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

            ReportIntent.SubmitReport -> {
                submitReport()
            }
        }
    }

    private fun loadReports() {

        val reports = listOf(

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
                status = ReportStatus.IN_PROGRESS,
                hasPhoto = false
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

        _state.update {
            it.copy(
                isLoading = false,
                reports = reports
            )
        }
    }

    private fun submitReport() {

        val currentState = _state.value

        if (
            currentState.selectedCategory == null ||
            currentState.description.isBlank()
        ) {
            return
        }

        _state.update {
            it.copy(
                isSubmitting = false,
                reportSubmitted = true
            )
        }
    }
}