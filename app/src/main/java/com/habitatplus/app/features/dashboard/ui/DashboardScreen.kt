package com.habitatplus.app.features.dashboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.habitatplus.app.core.components.*
import com.habitatplus.app.features.dashboard.intent.DashboardIntent
import com.habitatplus.app.features.dashboard.model.DashboardNavigation
import com.habitatplus.app.features.dashboard.model.DashboardTab
import com.habitatplus.app.features.dashboard.state.DashboardState
import com.habitatplus.app.ui.components.HabitatDestination
import com.habitatplus.app.ui.components.HabitatScaffold

@Composable
fun DashboardScreen(
    state: DashboardState,
    onIntent: (DashboardIntent) -> Unit,
    onAnnouncements: () -> Unit,
    onAnnouncement: (String) -> Unit,
    onParking: () -> Unit,
    onAreas: () -> Unit,
    onReports: () -> Unit,
    onProfile: () -> Unit,
    onDestinationClick: (HabitatDestination) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val navigation = state.pendingNavigation

    LaunchedEffect(navigation) {
        if (navigation != null) {
            // Limpiamos la solicitud para que no se repita al volver.
            onIntent(
                DashboardIntent.NavigationHandled(navigation)
            )

            when (navigation) {
                DashboardNavigation.Announcements -> {
                    onAnnouncements()
                }

                is DashboardNavigation.Announcement -> {
                    onAnnouncement(navigation.id)
                }

                DashboardNavigation.Parking -> {
                    onParking()
                }

                DashboardNavigation.Areas -> {
                    onAreas()
                }

                DashboardNavigation.Reports -> {
                    onReports()
                }

                DashboardNavigation.Profile -> {
                    onProfile()
                }

                is DashboardNavigation.Tab -> {
                    onDestinationClick(
                        navigation.destination.toHabitatDestination()
                    )
                }
            }
        }
    }

    HabitatScaffold(
        title = "Inicio",
        selectedDestination = HabitatDestination.HOME,
        onDestinationClick = { destination ->
            onIntent(
                DashboardIntent.SelectTab(
                    destination.toDashboardTab()
                )
            )
        }
    ) { innerPadding ->
        ResidentPage(
            modifier = modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
            if (!state.isLoading) {
                ResidentTitle(
                    text = "${state.greeting}, ${state.userName}",
                    subtitle = state.residence
                )
            }

            TextButton(
                onClick = {
                    onIntent(DashboardIntent.OpenProfile)
                }
            ) {
                Text("Mi perfil")
            }

            if (state.isOffline) {
                ResidentOfflineNotice()
            }

            when {
                state.isLoading -> {
                    ResidentLoading()
                }

                state.error != null -> {
                    ResidentError(
                        message = state.error,
                        onRetry = {
                            onIntent(DashboardIntent.Refresh)
                        }
                    )
                }

                else -> {
                    DashboardAnnouncementSection(
                        state = state,
                        onIntent = onIntent
                    )

                    DashboardParkingSection(
                        state = state,
                        onIntent = onIntent
                    )

                    DashboardReservationSection(
                        state = state,
                        onIntent = onIntent
                    )

                    DashboardReportsSection(
                        state = state,
                        onIntent = onIntent
                    )

                    DashboardMaintenanceSection(
                        state = state
                    )

                    if (state.lastUpdated.isNotBlank()) {
                        Text(
                            text = "Última actualización: ${state.lastUpdated}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    TextButton(
                        onClick = {
                            onIntent(DashboardIntent.Refresh)
                        }
                    ) {
                        Text("Actualizar")
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardAnnouncementSection(
    state: DashboardState,
    onIntent: (DashboardIntent) -> Unit
) {
    val announcement = state.announcement

    ResidentCard {
        if (announcement == null) {
            Text("No existen anuncios recientes.")
        } else {
            if (announcement.urgent) {
                ResidentBadge(
                    text = "URGENTE",
                    tone = ResidentBadgeTone.ORANGE
                )
            }

            Text(
                text = announcement.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = announcement.schedule,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = announcement.description,
                style = MaterialTheme.typography.bodyMedium
            )

            TextButton(
                onClick = {
                    onIntent(
                        DashboardIntent.OpenAnnouncement(
                            announcement.id
                        )
                    )
                }
            ) {
                Text("Ver anuncio")
            }
        }

        TextButton(
            onClick = {
                onIntent(DashboardIntent.OpenAnnouncements)
            }
        ) {
            Text("Todos los anuncios")
        }
    }
}

@Composable
private fun DashboardParkingSection(
    state: DashboardState,
    onIntent: (DashboardIntent) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ResidentCard(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            Text(
                text = "MI PARQUEO",
                style = MaterialTheme.typography.labelMedium
            )

            if (state.parkings.isEmpty()) {
                Text("Sin parqueo asignado")
            }

            state.parkings.forEach { parking ->
                Text(
                    text = parking.space,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                ResidentBadge(
                    text = if (parking.active) "Activo" else "Inactivo",
                    tone = if (parking.active) {
                        ResidentBadgeTone.GREEN
                    } else {
                        ResidentBadgeTone.ORANGE
                    }
                )

                Text(
                    text = parking.location,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            TextButton(
                onClick = {
                    onIntent(DashboardIntent.OpenParking)
                }
            ) {
                Text("Consultar")
            }
        }

        ResidentCard(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            ResidentBadge("Visitas")

            Text(
                text = state.freeVisitorSpaces.toString(),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "de ${state.totalVisitorSpaces} libres",
                style = MaterialTheme.typography.bodyMedium
            )

            val fraction = if (state.totalVisitorSpaces > 0) {
                (
                        state.freeVisitorSpaces.toFloat() /
                                state.totalVisitorSpaces
                        ).coerceIn(0f, 1f)
            } else {
                0f
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction)
                        .fillMaxHeight()
                        .background(
                            MaterialTheme.colorScheme.primary
                        )
                )
            }

            TextButton(
                onClick = {
                    onIntent(DashboardIntent.OpenParking)
                }
            ) {
                Text("Disponibilidad")
            }
        }
    }
}

@Composable
private fun DashboardReservationSection(
    state: DashboardState,
    onIntent: (DashboardIntent) -> Unit
) {
    val reservation = state.reservation

    ResidentCard {
        if (reservation == null) {
            Text(
                text = "Próxima reserva",
                style = MaterialTheme.typography.titleLarge
            )

            Text("No tienes reservas.")
        } else {
            Text(
                text = reservation.area,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = reservation.schedule,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            ResidentBadge(
                text = reservation.status,
                tone = ResidentBadgeTone.GREEN
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Reserva de área común",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = "Código de acceso: #${reservation.accessCode}"
                    )
                }
            }
        }

        OutlinedButton(
            onClick = {
                onIntent(DashboardIntent.OpenAreas)
            }
        ) {
            Text(
                text = if (reservation == null) {
                    "Consultar áreas"
                } else {
                    "Ver reserva"
                }
            )
        }
    }
}

@Composable
private fun DashboardReportsSection(
    state: DashboardState,
    onIntent: (DashboardIntent) -> Unit
) {
    ResidentCard {
        Text(
            text = "Mis reportes (${state.reports.size})",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        if (state.reports.isEmpty()) {
            Text("No tienes reportes activos.")
        }

        state.reports.forEach { report ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = report.title,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = "Ticket #${report.id} · ${report.detail}",
                        style = MaterialTheme.typography.bodySmall
                    )

                    ResidentBadge(
                        text = report.status,
                        tone = if (report.status == "Pendiente") {
                            ResidentBadgeTone.ORANGE
                        } else {
                            ResidentBadgeTone.BLUE
                        }
                    )
                }
            }
        }

        TextButton(
            onClick = {
                onIntent(DashboardIntent.OpenReports)
            }
        ) {
            Text("Ver detalles")
        }
    }
}

@Composable
private fun DashboardMaintenanceSection(
    state: DashboardState
) {
    val maintenance = state.maintenance ?: return

    ResidentCard {
        Text(
            text = "Mantenimiento general",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = maintenance.date,
            color = MaterialTheme.colorScheme.primary
        )

        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = maintenance.title,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = maintenance.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

private fun HabitatDestination.toDashboardTab(): DashboardTab {
    return when (this) {
        HabitatDestination.HOME -> DashboardTab.HOME
        HabitatDestination.PARKING -> DashboardTab.PARKING
        HabitatDestination.REPORTS -> DashboardTab.REPORTS
        HabitatDestination.AREAS -> DashboardTab.AREAS
        HabitatDestination.PROFILE -> DashboardTab.PROFILE
    }
}

private fun DashboardTab.toHabitatDestination(): HabitatDestination {
    return when (this) {
        DashboardTab.HOME -> HabitatDestination.HOME
        DashboardTab.PARKING -> HabitatDestination.PARKING
        DashboardTab.REPORTS -> HabitatDestination.REPORTS
        DashboardTab.AREAS -> HabitatDestination.AREAS
        DashboardTab.PROFILE -> HabitatDestination.PROFILE
    }
}