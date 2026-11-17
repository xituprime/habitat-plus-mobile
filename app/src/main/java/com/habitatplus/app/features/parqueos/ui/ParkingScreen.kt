package com.habitatplus.app.features.parqueos.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitatplus.app.features.parqueos.intent.ParkingIntent
import com.habitatplus.app.features.parqueos.model.ParkingStatus
import com.habitatplus.app.features.parqueos.state.ParkingState
import com.habitatplus.app.ui.components.HabitatDestination
import com.habitatplus.app.ui.components.HabitatScaffold
import com.habitatplus.app.ui.theme.*


@Composable
fun ParkingScreen(
    state: ParkingState = ParkingState(),
    onIntent: (ParkingIntent) -> Unit = {},
    onReservationClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    onDestinationClick: (HabitatDestination) -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    unreadCount: Int = 0
) {

    val colors = MaterialTheme.colorScheme

    HabitatScaffold(
        title = "Parqueos",
        selectedDestination = HabitatDestination.PARKING,
        onDestinationClick = onDestinationClick,
        onNotificationsClick = onNotificationsClick,
        unreadCount = unreadCount
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            SectionTitle("Mi Parqueo Asignado")

            ParkingCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "P-12",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.onSurface
                        )

                        Text(
                            text = "Apartamento A-203",
                            fontSize = 11.sp,
                            color = colors.onSurfaceVariant
                        )
                    }

                    StatusChip(
                        text = "Activo",
                        color = ParkingAvailable,
                        background = ParkingAvailableContainer
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = colors.outlineVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        SmallLabel("UBICACIÓN")

                        Text(
                            text = "Sótano 1",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = colors.onSurface
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        SmallLabel("PLACA ASIGNADA")

                        Surface(
                            color = colors.primaryContainer,
                            contentColor = colors.onPrimaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "HAB-892",
                                modifier = Modifier.padding(
                                    horizontal = 10.dp,
                                    vertical = 5.dp
                                ),
                                color = colors.onPrimaryContainer,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionTitle("Parqueos para Visitantes")

                StatusChip(
                    text = "${state.visitorParkings.count {
                        it.status == ParkingStatus.AVAILABLE
                    }} disponibles",
                    color = ParkingAvailable,
                    background = ParkingAvailableContainer
                )
            }

            state.visitorParkings.forEach { parking ->
                val available =
                    parking.status == ParkingStatus.AVAILABLE

                val occupied =
                    parking.status == ParkingStatus.OCCUPIED

                VisitorCard(
                    code = parking.id,
                    location = if (parking.id == "V-03") {
                        "Sótano 1"
                    } else {
                        "Nivel Calle"
                    },
                    status = when (parking.status) {
                        ParkingStatus.AVAILABLE -> "Disponible"
                        ParkingStatus.OCCUPIED -> "Ocupado"
                        ParkingStatus.MAINTENANCE -> "Mantenimiento"
                        else -> "Asignado"
                    },
                    statusColor = when {
                        available -> ParkingAvailable
                        occupied -> ParkingOccupied
                        else -> ParkingMaintenance
                    },
                    statusBackground = when {
                        available -> ParkingAvailableContainer
                        occupied -> ParkingOccupiedContainer
                        else -> ParkingMaintenanceContainer
                    },
                    message = when {
                        available -> "Listo para asignación inmediata"

                        occupied ->
                            "Ocupado hasta ${
                                parking.occupiedUntil ?: "hora no disponible"
                            }"

                        else -> "Mantenimiento preventivo programado"
                    },
                    showReserve = available,
                    onReserveClick = {
                        onIntent(
                            ParkingIntent.StartReservation(parking.id)
                        )
                        onReservationClick()
                    }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SectionTitle("Últimas Reservas")

                Text(
                    text = "Ver historial",
                    color = colors.primary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable {
                        onHistoryClick()
                    }
                )
            }

            state.recentHistory.take(2).forEach { reservation ->
                ReservationSummaryCard(
                    name = reservation.visitorName,
                    parking = reservation.parkingId,
                    date = "${reservation.date} · ${reservation.time}",
                    status = reservation.status
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        state.visitorParkings
                            .firstOrNull {
                                it.status == ParkingStatus.AVAILABLE
                            }
                            ?.let { parking ->
                                onIntent(
                                    ParkingIntent.StartReservation(parking.id)
                                )
                                onReservationClick()
                            }
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary,
                        contentColor = colors.onPrimary
                    )
                ) {
                    Text("+  Nueva Reserva")
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun VisitorCard(
    onReserveClick: () -> Unit = {},
    code: String,
    location: String,
    status: String,
    statusColor: Color,
    statusBackground: Color,
    message: String,
    showReserve: Boolean = false
) {
    val colors = MaterialTheme.colorScheme

    ParkingCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = code,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.onSurface
                )

                Text(
                    text = location,
                    fontSize = 10.sp,
                    color = colors.onSurfaceVariant
                )
            }

            StatusChip(
                text = status,
                color = statusColor,
                background = statusBackground
            )
        }

        Spacer(Modifier.height(10.dp))

        if (showReserve) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = message,
                    fontSize = 10.sp,
                    color = colors.onSurfaceVariant,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )

                Button(
                    onClick = onReserveClick,
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(
                        horizontal = 14.dp,
                        vertical = 4.dp
                    ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary,
                        contentColor = colors.onPrimary
                    )
                ) {
                    Text(
                        text = "Reservar",
                        fontSize = 10.sp
                    )
                }
            }
        } else {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = colors.surfaceVariant,
                contentColor = colors.onSurfaceVariant,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = message,
                    modifier = Modifier.padding(10.dp),
                    fontSize = 10.sp,
                    color = colors.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ReservationSummaryCard(
    name: String,
    parking: String,
    date: String,
    status: String
) {
    val colors = MaterialTheme.colorScheme

    ParkingCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.onSurface
                )

                Text(
                    text = "Parqueo $parking",
                    fontSize = 9.sp,
                    color = colors.onSurfaceVariant
                )

                Text(
                    text = date,
                    fontSize = 9.sp,
                    color = colors.onSurfaceVariant
                )
            }

            StatusChip(
                text = status,
                color = if (status == "Activa") {
                    ParkingAvailable
                } else {
                    colors.onSecondaryContainer
                },
                background = if (status == "Activa") {
                    ParkingAvailableContainer
                } else {
                    colors.secondaryContainer
                }
            )
        }
    }
}

@Composable
private fun ParkingCard(
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

@Composable
private fun StatusChip(
    text: String,
    color: Color,
    background: Color
) {
    Surface(
        color = background,
        contentColor = color,
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = 9.dp,
                vertical = 5.dp
            ),
            color = color,
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun SectionTitle(
    text: String
) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun SmallLabel(
    text: String
) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 8.sp
    )
}

@Preview(
    name = "Parqueos - Claro",
    showBackground = true,
    widthDp = 411,
    heightDp = 891
)
@Composable
private fun ParkingScreenLightPreview() {
    ResidentTheme(darkTheme = false) {
        ParkingScreen()
    }
}

@Preview(
    name = "Parqueos - Oscuro",
    showBackground = true,
    widthDp = 411,
    heightDp = 891
)
@Composable
private fun ParkingScreenDarkPreview() {
    ResidentTheme(darkTheme = true) {
        ParkingScreen()
    }
}