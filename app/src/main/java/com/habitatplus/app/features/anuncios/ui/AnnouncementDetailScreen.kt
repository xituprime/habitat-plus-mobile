package com.habitatplus.app.features.anuncios.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.habitatplus.app.core.components.*
import com.habitatplus.app.features.anuncios.model.Announcement

@Composable
fun AnnouncementDetailScreen(
    announcement: Announcement?,
    isLoading: Boolean,
    error: String?,
    isOffline: Boolean,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    announcementImage: Painter? = null
) {
    ResidentPage(modifier) {
        ResidentTitle(
            text = "Detalle del anuncio"
        )

        if (isOffline) {
            ResidentOfflineNotice()
        }

        when {
            isLoading -> {
                ResidentLoading()
            }

            error != null -> {
                ResidentError(
                    message = error,
                    onRetry = onRetry
                )
            }

            announcement == null -> {
                ResidentCard {
                    Text(
                        text = "Anuncio no disponible",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "El anuncio pudo haber sido retirado " +
                                "o todavía no está disponible.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            else -> {
                AnnouncementDetailCard(
                    announcement = announcement,
                    announcementImage = announcementImage
                )
            }
        }

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver a anuncios")
        }
    }
}

@Composable
private fun AnnouncementDetailCard(
    announcement: Announcement,
    announcementImage: Painter?
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
        if (announcementImage != null) {
            Image(
                painter = announcementImage,
                contentDescription = "Imagen del anuncio",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = announcement.serviceLabel
                            ?: announcement.category,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Text(
                        text = "Comunicado de administración",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // En vertical para evitar recortes en pantallas estrechas.
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ResidentBadge(
                    text = announcement.serviceLabel
                        ?: announcement.category
                )

                if (announcement.priority) {
                    ResidentBadge(
                        text = "Importante",
                        tone = ResidentBadgeTone.RED
                    )
                }
            }

            Text(
                text = announcement.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Publicado por ${announcement.author}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                val publicationLabel = listOfNotNull(
                    announcement.date,
                    announcement.publishedTime
                ).joinToString(" · ")

                Text(
                    text = publicationLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            announcement.suspensionSchedule?.let { schedule ->
                SuspensionWindow(
                    schedule = schedule,
                    duration = announcement.suspensionDuration
                )
            }

            Text(
                text = announcement.body,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun SuspensionWindow(
    schedule: String,
    duration: String?
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Ventana de suspensión",
                style = MaterialTheme.typography.labelLarge
            )

            Text(
                text = schedule,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (!duration.isNullOrBlank()) {
                ResidentBadge(
                    text = duration
                )
            }
        }
    }
}