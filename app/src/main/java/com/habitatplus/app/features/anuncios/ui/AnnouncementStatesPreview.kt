package com.habitatplus.app.features.anuncios.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.habitatplus.app.features.anuncios.state.AnnouncementsState
import com.habitatplus.app.ui.theme.ResidentTheme

@Preview(
    name = "Lista - Cargando",
    showBackground = true,
    widthDp = 412,
    heightDp = 850
)
@Composable
private fun AnnouncementsLoadingPreview() {
    ResidentTheme(darkTheme = false) {
        AnnouncementsScreen(
            state = AnnouncementsState(
                isLoading = true
            ),
            onIntent = {},
            onAnnouncement = {}
        )
    }
}

@Preview(
    name = "Lista - Error",
    showBackground = true,
    widthDp = 412,
    heightDp = 850
)
@Composable
private fun AnnouncementsErrorPreview() {
    ResidentTheme(darkTheme = false) {
        AnnouncementsScreen(
            state = AnnouncementsState(
                isLoading = false,
                error = "No se pudieron cargar los anuncios. Intenta nuevamente."
            ),
            onIntent = {},
            onAnnouncement = {}
        )
    }
}

@Preview(
    name = "Lista - Sin resultados",
    showBackground = true,
    widthDp = 412,
    heightDp = 850
)
@Composable
private fun AnnouncementsEmptyPreview() {
    ResidentTheme(darkTheme = false) {
        AnnouncementsScreen(
            state = AnnouncementsState(
                isLoading = false,
                query = "zzzzzz",
                announcements = emptyList(),
                visibleAnnouncements = emptyList()
            ),
            onIntent = {},
            onAnnouncement = {}
        )
    }
}

@Preview(
    name = "Detalle - Cargando",
    showBackground = true,
    widthDp = 412,
    heightDp = 850
)
@Composable
private fun AnnouncementDetailLoadingPreview() {
    ResidentTheme(darkTheme = false) {
        AnnouncementDetailScreen(
            announcement = null,
            isLoading = true,
            error = null,
            isOffline = false,
            onRetry = {},
            onBack = {}
        )
    }
}

@Preview(
    name = "Detalle - Error",
    showBackground = true,
    widthDp = 412,
    heightDp = 850
)
@Composable
private fun AnnouncementDetailErrorPreview() {
    ResidentTheme(darkTheme = false) {
        AnnouncementDetailScreen(
            announcement = null,
            isLoading = false,
            error = "No se pudo cargar este anuncio. Intenta nuevamente.",
            isOffline = false,
            onRetry = {},
            onBack = {}
        )
    }
}

@Preview(
    name = "Detalle - No disponible",
    showBackground = true,
    widthDp = 412,
    heightDp = 850
)
@Composable
private fun AnnouncementDetailUnavailablePreview() {
    ResidentTheme(darkTheme = false) {
        AnnouncementDetailScreen(
            announcement = null,
            isLoading = false,
            error = null,
            isOffline = false,
            onRetry = {},
            onBack = {}
        )
    }
}