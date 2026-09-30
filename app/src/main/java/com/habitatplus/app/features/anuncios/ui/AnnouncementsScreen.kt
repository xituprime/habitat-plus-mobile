package com.habitatplus.app.features.anuncios.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.habitatplus.app.core.components.*
import com.habitatplus.app.features.anuncios.intent.AnnouncementsIntent
import com.habitatplus.app.features.anuncios.model.Announcement
import com.habitatplus.app.features.anuncios.model.AnnouncementFilter
import com.habitatplus.app.features.anuncios.state.AnnouncementsState

@Composable
fun AnnouncementsScreen(
    state: AnnouncementsState,
    onIntent: (AnnouncementsIntent) -> Unit,
    onAnnouncement: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    ResidentPage(modifier) {
        ResidentTitle(
            text = "Anuncios",
            subtitle = "Comunicados de tu comunidad"
        )

        if (state.isOffline) {
            ResidentOfflineNotice()
        }

        OutlinedTextField(
            value = state.query,
            onValueChange = {
                onIntent(AnnouncementsIntent.Search(it))
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Buscar comunicados o avisos")
            },
            singleLine = true,
            shape = MaterialTheme.shapes.small,
            trailingIcon = {
                if (state.query.isNotEmpty()) {
                    TextButton(
                        onClick = {
                            onIntent(
                                AnnouncementsIntent.Search("")
                            )
                        }
                    ) {
                        Text("Limpiar")
                    }
                }
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AnnouncementFilter.entries.forEach { filter ->
                FilterChip(
                    selected = state.filter == filter,
                    onClick = {
                        onIntent(
                            AnnouncementsIntent.SelectFilter(filter)
                        )
                    },
                    label = {
                        Text(filter.label)
                    }
                )
            }
        }

        when {
            state.isLoading -> {
                ResidentLoading()
            }

            state.error != null -> {
                ResidentError(
                    message = state.error,
                    onRetry = {
                        onIntent(AnnouncementsIntent.Refresh)
                    }
                )
            }

            state.visibleAnnouncements.isEmpty() -> {
                ResidentCard {
                    Text(
                        text = "No hay anuncios para mostrar",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Prueba otra búsqueda o selecciona " +
                                "otra categoría.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            else -> {
                state.visibleAnnouncements.forEach { announcement ->
                    AnnouncementListCard(
                        announcement = announcement,
                        onOpen = {
                            onAnnouncement(announcement.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AnnouncementListCard(
    announcement: Announcement,
    onOpen: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            if (announcement.priority) {
                Box(
                    modifier = Modifier
                        .width(5.dp)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.error)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (announcement.priority) {
                    ResidentBadge(
                        text = "Importante",
                        tone = ResidentBadgeTone.RED
                    )
                }

                ResidentBadge(
                    text = announcement.category
                )

                Text(
                    text = announcement.date,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = announcement.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = announcement.summary,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = announcement.author,
                    style = MaterialTheme.typography.labelLarge
                )

                TextButton(
                    onClick = onOpen
                ) {
                    Text("Ver detalle")
                }
            }
        }
    }
}