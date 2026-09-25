package com.habitatplus.app.features.dashboard.viewmodel

import androidx.lifecycle.ViewModel
import com.habitatplus.app.features.dashboard.intent.DashboardIntent
import com.habitatplus.app.features.dashboard.state.DashboardState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.habitatplus.app.features.dashboard.model.DashboardAnnouncement
import com.habitatplus.app.features.dashboard.model.DashboardParking
import com.habitatplus.app.features.dashboard.model.DashboardReservation
import com.habitatplus.app.features.dashboard.model.DashboardReport
import com.habitatplus.app.features.dashboard.model.DashboardMaintenance
class DashboardViewModel : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())

    val state: StateFlow<DashboardState> = _state.asStateFlow()

    fun onIntent(intent: DashboardIntent) {
        when (intent) {
            DashboardIntent.LoadDashboard -> loadDashboard()
            DashboardIntent.RefreshDashboard -> loadDashboard()
        }
    }

    private fun loadDashboard() {
        // Datos de prueba; todavía no se consulta Firebase.
        _state.update { currentState ->
            currentState.copy(
                isLoading = false,
                userName = "Kenett",
                condominiumName = "Condominio Las Acacias",
                apartmentNumber = "A-203",
                errorMessage = null,
                announcement = DashboardAnnouncement(
                    title = "Corte de agua programado",
                    schedule = "Hoy · 02:00 PM a 05:00 PM",
                    description = "Mantenimiento en la red de bombeo de Torre A. " +
                            "Sugerimos almacenar agua para uso esencial con antelación.",
                    isUrgent = true
                ),
                parking = DashboardParking(
                    assignedSpace = "P-12",
                    level = "Nivel Subsuelo 1",
                    isActive = true,
                    availableVisitorSpaces = 3,
                    totalVisitorSpaces = 8
                ),
                reservation = DashboardReservation(
                    areaName = "Cancha Sintética",
                    schedule = "Hoy · 06:00 PM (1 hora)",
                    status = "Confirmada",
                    accessCode = "4092"
                ),
                reports = listOf(
                    DashboardReport(
                        id = "REP-104",
                        title = "Luminaria parpadeando pasillo",
                        detail = "En revisión",
                        status = "Pendiente"
                    ),
                    DashboardReport(
                        id = "REP-098",
                        title = "Ruido extractor Torre Central",
                        detail = "En asignación",
                        status = "En trámite"
                    )
                ),
                    maintenance = DashboardMaintenance(
                        title = "Revisión preventiva de ascensores",
                        dateLabel = "Jueves 24 Oct",
                        description = "Ascensor 1 de Torre A permanecerá inactivo " +
                                "entre las 09:00 AM y las 12:00 PM. " +
                                "Se recomienda usar el ascensor de servicio."
                    )
                )
        }
    }
}