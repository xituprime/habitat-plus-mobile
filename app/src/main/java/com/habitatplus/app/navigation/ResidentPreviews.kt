package com.habitatplus.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.habitatplus.app.features.anuncios.model.Announcement
import com.habitatplus.app.features.anuncios.state.AnnouncementsState
import com.habitatplus.app.features.anuncios.ui.AnnouncementDetailScreen
import com.habitatplus.app.features.anuncios.ui.AnnouncementsScreen
import com.habitatplus.app.features.configuracion.state.SettingsState
import com.habitatplus.app.features.configuracion.ui.SettingsScreen
import com.habitatplus.app.features.dashboard.model.DashboardAnnouncement
import com.habitatplus.app.features.dashboard.model.DashboardMaintenance
import com.habitatplus.app.features.dashboard.model.DashboardParking
import com.habitatplus.app.features.dashboard.model.DashboardReport
import com.habitatplus.app.features.dashboard.model.DashboardReservation
import com.habitatplus.app.features.dashboard.state.DashboardState
import com.habitatplus.app.features.dashboard.ui.DashboardScreen
import com.habitatplus.app.features.perfil.state.ProfileState
import com.habitatplus.app.features.perfil.ui.ProfileScreen
import com.habitatplus.app.ui.theme.ResidentTheme

private val previewAnnouncements = listOf(
    Announcement(
        id = "agua",
        title = "Corte programado de agua por mantenimiento general de bombas",
        category = "Mantenimiento",
        date = "14 de octubre de 2026",
        summary = "Interrupción preventiva del suministro hídrico " +
                "programada de 14:00 a 18:00 horas.",
        body = """
            Estimados residentes:

            Les informamos que se llevará a cabo el mantenimiento semestral de las bombas de presión hidroneumáticas y limpieza de cisternas centrales.

            El suministro de agua potable será suspendido temporalmente desde las 14:00 hasta las 18:00 horas.

            Agradecemos tomar las previsiones necesarias recolectando agua previamente.

            Durante los trabajos, mantenga cerradas las llaves de agua. La administración informará cuando el servicio haya sido restablecido.

            Gracias por su comprensión.
        """.trimIndent(),
        priority = true,
        publishedTime = "08:30 AM",
        serviceLabel = "Servicios Básicos",
        suspensionSchedule = "14:00 hrs – 18:00 hrs",
        suspensionDuration = "4 horas"
    ),
    Announcement(
        id = "fumigacion",
        title = "Fumigación de áreas verdes comunes",
        category = "Mantenimiento",
        date = "10 de octubre de 2026",
        summary = "Jornada preventiva en jardines perimetrales, " +
                "zona de juegos infantiles y pasillos.",
        body = "Respete la señalización durante los trabajos de fumigación."
    ),
    Announcement(
        id = "asamblea",
        title = "Asamblea ordinaria de propietarios",
        category = "Institucional",
        date = "05 de octubre de 2026",
        summary = "Convocatoria en el Salón Multiuso a las 19:30 horas.",
        body = "Se presentará el informe de administración."
    ),
    Announcement(
        id = "mascotas",
        title = "Reglamento de convivencia para mascotas",
        category = "Convivencia",
        date = "28 de septiembre de 2026",
        summary = "Recordatorio sobre correa, limpieza y uso " +
                "responsable de las áreas comunes.",
        body = "Recuerde utilizar correa y recoger los desechos de su mascota."
    )
)

private val previewDashboard = DashboardState(
    isLoading = false,
    greeting = "Buenos días",
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

private val previewProfile = ProfileState(
    isLoading = false,
    name = "Kenett Ortega",
    initials = "KO",
    role = "Residente",
    condominium = "Condominio Las Acacias",
    tower = "Torre A",
    apartment = "A-203",
    email = "kenett@example.com",
    phone = "+502 5555-0100",
    verified = true
)

@Preview(
    name = "01 - Inicio",
    group = "Pantallas Kenett",
    showBackground = true,
    widthDp = 412,
    heightDp = 2200
)
@Composable
private fun DashboardScreenPreview() {
    ResidentTheme(darkTheme = false) {
        DashboardScreen(
            state = previewDashboard,
            onIntent = {},
            onAnnouncements = {},
            onAnnouncement = {},
            onParking = {},
            onAreas = {},
            onReports = {},
            onProfile = {}
        )
    }
}

@Preview(
    name = "02 - Anuncios",
    group = "Pantallas Kenett",
    showBackground = true,
    widthDp = 412,
    heightDp = 1600
)
@Composable
private fun AnnouncementsScreenPreview() {
    ResidentTheme(darkTheme = false) {
        AnnouncementsScreen(
            state = AnnouncementsState(
                isLoading = false,
                announcements = previewAnnouncements,
                visibleAnnouncements = previewAnnouncements
            ),
            onIntent = {},
            onAnnouncement = {}
        )
    }
}

@Preview(
    name = "03 - Detalle de anuncio",
    group = "Pantallas Kenett",
    showBackground = true,
    widthDp = 412,
    heightDp = 1500
)
@Composable
private fun AnnouncementDetailScreenPreview() {
    ResidentTheme(darkTheme = false) {
        AnnouncementDetailScreen(
            announcement = previewAnnouncements.first(),
            isLoading = false,
            error = null,
            isOffline = false,
            onRetry = {},
            onBack = {}
        )
    }
}

@Preview(
    name = "04 - Mi perfil",
    group = "Pantallas Kenett",
    showBackground = true,
    widthDp = 412,
    heightDp = 1000
)
@Composable
private fun ProfileScreenPreview() {
    ResidentTheme(darkTheme = false) {
        ProfileScreen(
            state = previewProfile,
            onIntent = {},
            onSettings = {}
        )
    }
}

@Preview(
    name = "05 - Configuración",
    group = "Pantallas Kenett",
    showBackground = true,
    widthDp = 412,
    heightDp = 1000
)
@Composable
private fun SettingsScreenPreview() {
    ResidentTheme(darkTheme = false) {
        SettingsScreen(
            state = SettingsState(),
            userName = previewProfile.name,
            userEmail = previewProfile.email,
            userInitials = previewProfile.initials,
            residence = "${previewProfile.tower} · ${previewProfile.apartment}",
            onIntent = {},
            onSignOut = {}
        )
    }
}