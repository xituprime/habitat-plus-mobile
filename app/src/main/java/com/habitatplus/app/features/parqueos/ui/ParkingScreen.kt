
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
    residentApartment: String = "",
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
                            text = "Parqueo residencial no asignado",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.onSurface
                        )

                        Text(
                            text = if (
                                residentApartment.isBlank() ||
                                residentApartment == "Sin asignar"
                            ) {
                                "Residencia pendiente de asignación"
                            } else {
                                "Apartamento $residentApartment"
                            },
                            fontSize = 11.sp,
                            color = colors.onSurfaceVariant
                        )
                    }

                    StatusChip(
                        text = "● Sin parqueo",
                        status = ChipStatus.NEUTRAL
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
                            text = "No registrada",
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
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "No registrada",
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
                    status = ChipStatus.SUCCESS
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
                    chipStatus = when {
                        available -> ChipStatus.SUCCESS
                        occupied -> ChipStatus.ERROR
                        else -> ChipStatus.WARNING
                    },
                    message = when {
                        available -> "Selecciona fecha y horario para reservar"
                        occupied ->
                            "Ocupado hasta ${parking.occupiedUntil ?: "hora no disponible"}"
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
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
                enabled = state.visitorParkings.any {
                    it.status == ParkingStatus.AVAILABLE
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("+ Nueva reserva")
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

private enum class ChipStatus {
    SUCCESS, ERROR, WARNING, NEUTRAL
}

@Composable
private fun StatusChip(
    text: String,
    status: ChipStatus
) {
    val colors = MaterialTheme.colorScheme

    val foreground = when (status) {
        ChipStatus.SUCCESS -> Color(0xFF22C55E)
        ChipStatus.ERROR -> colors.error
        ChipStatus.WARNING -> Color(0xFFF59E0B)
        ChipStatus.NEUTRAL -> colors.onSurfaceVariant
    }

    Surface(
        color = foreground.copy(alpha = 0.15f),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = 9.dp,
                vertical = 5.dp
            ),
            color = when (status) {
                ChipStatus.SUCCESS -> {
                    if (colors.background == Color(0xFF0F172A)) {
                        Color(0xFF86EFAC)
                    } else {
                        Color(0xFF166534)
                    }
                }
                ChipStatus.WARNING -> {
                    if (colors.background == Color(0xFF0F172A)) {
                        Color(0xFFFDE68A)
                    } else {
                        Color(0xFF92400E)
                    }
                }
                else -> foreground
            },
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun VisitorCard(
    code: String,
    location: String,
    status: String,
    chipStatus: ChipStatus,
    message: String,
    showReserve: Boolean,
    onReserveClick: () -> Unit
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
                    color = colors.onSurface,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )

                Text(
                    text = location,
                    color = colors.onSurfaceVariant,
                    fontSize = 10.sp
                )
            }

            StatusChip(
                text = "● $status",
                status = chipStatus
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
                    color = colors.onSurfaceVariant,
                    fontSize = 10.sp,
                    modifier = Modifier.weight(1f)
                )

                Spacer(Modifier.width(8.dp))

                Button(
                    onClick = onReserveClick,
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(
                        horizontal = 14.dp,
                        vertical = 4.dp
                    )
                ) {
                    Text("Reservar", fontSize = 10.sp)
                }
            }
        } else {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = colors.surfaceContainer,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = message,
                    modifier = Modifier.padding(10.dp),
                    color = colors.onSurfaceVariant,
                    fontSize = 10.sp
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    color = colors.onSurface,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "Parqueo $parking",
                    color = colors.onSurfaceVariant,
                    fontSize = 9.sp
                )

                Text(
                    text = date,
                    color = colors.onSurfaceVariant,
                    fontSize = 9.sp
                )
            }

            StatusChip(
                text = status,
                status = if (status == "Activa") {
                    ChipStatus.SUCCESS
                } else {
                    ChipStatus.NEUTRAL
                }
            )
        }
    }
}

@Composable
private fun ParkingCard(
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.surface,
            contentColor = colors.onSurface
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
private fun SectionTitle(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun SmallLabel(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 8.sp
    )
}

@Preview(name = "Parqueos claro", showBackground = true)
@Composable
private fun ParkingLightPreview() {
    HabitatPlusTheme(darkTheme = false) {
        ParkingScreen()
    }
}

@Preview(name = "Parqueos oscuro", showBackground = true)
@Composable
private fun ParkingDarkPreview() {
    HabitatPlusTheme(darkTheme = true) {
        ParkingScreen()
    }
}
