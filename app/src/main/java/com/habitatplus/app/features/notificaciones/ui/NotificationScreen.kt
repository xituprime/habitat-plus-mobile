
package com.habitatplus.app.features.notificaciones.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.habitatplus.app.features.notificaciones.model.NotificationModel
import com.habitatplus.app.features.notificaciones.model.NotificationType
import com.habitatplus.app.features.notificaciones.viewmodel.NotificationState
import com.habitatplus.app.ui.theme.ResidentTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotificationScreen(
    state: NotificationState,
    onBackClick: () -> Unit,
    onMarkAsRead: (String) -> Unit,
    onMarkAllAsRead: () -> Unit,
    onDelete: (String) -> Unit,
    onRefresh: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = onBackClick) {
                Text("Volver")
            }

            Text(
                text = "Notificaciones",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = colors.onBackground
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${state.unreadCount} sin leer",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onBackground
            )

            TextButton(
                onClick = onMarkAllAsRead,
                enabled = state.unreadCount > 0
            ) {
                Text("Marcar todas")
            }
        }

        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.error != null -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = state.error,
                        color = colors.error
                    )

                    Button(onClick = onRefresh) {
                        Text("Reintentar")
                    }
                }
            }

            state.notifications.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No tienes notificaciones.",
                        color = colors.onSurfaceVariant
                    )
                }
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = state.notifications,
                        key = { it.id }
                    ) { notification ->
                        NotificationItem(
                            notification = notification,
                            onMarkAsRead = {
                                onMarkAsRead(notification.id)
                            },
                            onDelete = {
                                onDelete(notification.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationItem(
    notification: NotificationModel,
    onMarkAsRead: () -> Unit,
    onDelete: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) {
                colors.surface
            } else {
                colors.primaryContainer
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(
                            color = if (notification.isRead) {
                                colors.outlineVariant
                            } else {
                                colors.primary
                            },
                            shape = CircleShape
                        )
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = notificationTypeLabel(notification.type),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (notification.isRead) {
                        colors.onSurfaceVariant
                    } else {
                        colors.onPrimaryContainer
                    }
                )
            }

            Text(
                text = notification.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (notification.isRead) {
                    colors.onSurface
                } else {
                    colors.onPrimaryContainer
                }
            )

            Text(
                text = notification.message,
                style = MaterialTheme.typography.bodyMedium,
                color = if (notification.isRead) {
                    colors.onSurfaceVariant
                } else {
                    colors.onPrimaryContainer
                }
            )

            Text(
                text = formatNotificationDate(notification.createdAt),
                style = MaterialTheme.typography.bodySmall,
                color = if (notification.isRead) {
                    colors.onSurfaceVariant
                } else {
                    colors.onPrimaryContainer
                }
            )

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (!notification.isRead) {
                    TextButton(onClick = onMarkAsRead) {
                        Text("Marcar leída")
                    }
                }

                TextButton(onClick = onDelete) {
                    Text("Eliminar")
                }
            }
        }
    }
}

private fun notificationTypeLabel(
    type: NotificationType
): String {
    return when (type) {
        NotificationType.REPORT -> "Reporte"
        NotificationType.PARKING -> "Parqueo"
        NotificationType.COMMON_AREA -> "Área común"
        NotificationType.ANNOUNCEMENT -> "Anuncio"
    }
}

private fun formatNotificationDate(
    timestamp: Long
): String {
    return SimpleDateFormat(
        "dd/MM/yyyy · HH:mm",
        Locale.getDefault()
    ).format(Date(timestamp))
}

@Preview(
    name = "Notificaciones - Claro",
    showBackground = true
)
@Composable
private fun NotificationScreenLightPreview() {
    ResidentTheme(darkTheme = false) {
        NotificationScreen(
            state = NotificationState(
                isLoading = false,
                notifications = listOf(
                    NotificationModel(
                        id = "preview-1",
                        userId = "preview",
                        type = NotificationType.REPORT,
                        title = "Reporte registrado",
                        message = "Tu reporte fue recibido.",
                        isRead = false
                    ),
                    NotificationModel(
                        id = "preview-2",
                        userId = "preview",
                        type = NotificationType.PARKING,
                        title = "Reserva confirmada",
                        message = "Tu reserva fue registrada.",
                        isRead = true
                    )
                )
            ),
            onBackClick = {},
            onMarkAsRead = {},
            onMarkAllAsRead = {},
            onDelete = {},
            onRefresh = {}
        )
    }
}

@Preview(
    name = "Notificaciones - Oscuro",
    showBackground = true
)
@Composable
private fun NotificationScreenDarkPreview() {
    ResidentTheme(darkTheme = true) {
        NotificationScreen(
            state = NotificationState(
                isLoading = false,
                notifications = listOf(
                    NotificationModel(
                        id = "preview-1",
                        userId = "preview",
                        type = NotificationType.ANNOUNCEMENT,
                        title = "Nuevo anuncio",
                        message = "Hay un comunicado disponible.",
                        isRead = false
                    )
                )
            ),
            onBackClick = {},
            onMarkAsRead = {},
            onMarkAllAsRead = {},
            onDelete = {},
            onRefresh = {}
        )
    }
}
