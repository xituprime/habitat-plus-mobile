package com.habitatplus.app.features.dashboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.habitatplus.app.core.components.*
import com.habitatplus.app.features.dashboard.intent.DashboardIntent
import com.habitatplus.app.features.dashboard.state.DashboardState

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
    modifier: Modifier = Modifier
) {
    ResidentPage(modifier) {
        ResidentTitle(
            text = "${state.greeting}, ${state.userName}",
            subtitle = state.residence
        )

        // Acceso de contenido; no es una barra superior.
        TextButton(onClick = onProfile) {
            Text("Mi perfil")
        }

        if (state.isOffline) {
            ResidentOfflineNotice()
        }

        when {
            state.isLoading -> ResidentLoading()

            state.error != null -> ResidentError(
                message = state.error,
                onRetry = { onIntent(DashboardIntent.Refresh) }
            )

            else -> {
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
                            onClick = { onAnnouncement(announcement.id) }
                        ) {
                            Text("Ver anuncio")
                        }
                    }

                    TextButton(onClick = onAnnouncements) {
                        Text("Todos los anuncios")
                    }
                }

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

                        TextButton(onClick = onParking) {
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
                            text = "${state.freeVisitorSpaces}",
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
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                        }

                        TextButton(onClick = onParking) {
                            Text("Disponibilidad")
                        }
                    }
                }

                ResidentCard {
                    val reservation = state.reservation

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
                                    text = "Código de acceso: " +
                                            "#${reservation.accessCode}"
                                )
                            }
                        }
                    }

                    OutlinedButton(onClick = onAreas) {
                        Text(
                            if (reservation == null) {
                                "Consultar áreas"
                            } else {
                                "Ver reserva"
                            }
                        )
                    }
                }

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

                    TextButton(onClick = onReports) {
                        Text("Ver detalles")
                    }
                }

                state.maintenance?.let { maintenance ->
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

                TextButton(
                    onClick = { onIntent(DashboardIntent.Refresh) }
                ) {
                    Text("Actualizar")
                }
            }
        }
    }
}