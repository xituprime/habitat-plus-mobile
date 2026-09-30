package com.habitatplus.app.features.dashboard.viewmodel

import androidx.lifecycle.ViewModel
import com.habitatplus.app.features.dashboard.model.DashboardAnnouncement
import com.habitatplus.app.features.dashboard.model.DashboardMaintenance
import com.habitatplus.app.features.dashboard.model.DashboardParking
import com.habitatplus.app.features.dashboard.model.DashboardReport
import com.habitatplus.app.features.dashboard.model.DashboardReservation
import com.habitatplus.app.features.dashboard.intent.DashboardIntent
import com.habitatplus.app.features.dashboard.state.DashboardState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar

class DashboardViewModel : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())

    val state: StateFlow<DashboardState> = _state.asStateFlow()

    init {
        onIntent(DashboardIntent.Load)
    }

    fun onIntent(intent: DashboardIntent) {
        when (intent) {
            DashboardIntent.Load,
            DashboardIntent.Refresh -> loadDemo()
        }
    }

    private fun loadDemo() {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        val greeting = when (hour) {
            in 5..11 -> "Buenos días"
            in 12..18 -> "Buenas tardes"
            else -> "Buenas noches"
        }

        _state.value = DashboardState(
            isLoading = false,
            greeting = greeting,
            userName = "Kenett",
            residence = "Condominio Las Acacias · A-203",

            announcement = DashboardAnnouncement(
                id = "agua",
                title = "Corte de agua programado",
                schedule = "14 Oct · 02:00 PM a 06:00 PM",
                description = "Mantenimiento en la red de bombeo de Torre A. " +
                        "Sugerimos almacenar agua para uso esencial con antelación.",
                urgent = true
            ),

            parkings = listOf(
                DashboardParking(
                    space = "P-12",
                    location = "Nivel Subsuelo 1",
                    active = true
                )
            ),

            freeVisitorSpaces = 3,
            totalVisitorSpaces = 8,

            reservation = DashboardReservation(
                area = "Cancha Sintética",
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
                    status = "En proceso"
                )
            ),

            maintenance = DashboardMaintenance(
                title = "Revisión preventiva de ascensores",
                date = "24 Oct",
                description = "Ascensor 1 de Torre A permanecerá inactivo " +
                        "entre las 09:00 AM y las 12:00 PM. " +
                        "Se recomienda usar el ascensor de servicio."
            )
        )
    }
}