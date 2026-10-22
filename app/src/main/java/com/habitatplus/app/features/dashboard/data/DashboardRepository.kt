package com.habitatplus.app.features.dashboard.data

import com.habitatplus.app.features.dashboard.model.DashboardAnnouncement
import com.habitatplus.app.features.dashboard.model.DashboardMaintenance
import com.habitatplus.app.features.dashboard.model.DashboardParking
import com.habitatplus.app.features.dashboard.model.DashboardReport
import com.habitatplus.app.features.dashboard.model.DashboardReservation

data class DashboardData(
    val announcement: DashboardAnnouncement?,
    val parkings: List<DashboardParking>,
    val freeVisitorSpaces: Int,
    val totalVisitorSpaces: Int,
    val reservation: DashboardReservation?,
    val reports: List<DashboardReport>,
    val maintenance: DashboardMaintenance?
)

interface DashboardRepository {
    fun getDashboard(): DashboardData
}

/**
 * Resumen ficticio para la entrega.
 * No consulta los módulos de Parqueos, Reportes ni Áreas comunes.
 */
object FakeDashboardRepository : DashboardRepository {

    override fun getDashboard(): DashboardData {
        return DashboardData(
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