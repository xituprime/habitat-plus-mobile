package com.habitatplus.app.features.parqueos.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitatplus.app.R
import com.habitatplus.app.features.parqueos.intent.ParkingIntent
import com.habitatplus.app.features.parqueos.state.ParkingState
import com.habitatplus.app.ui.components.HabitatDestination
import com.habitatplus.app.ui.components.HabitatScaffold
import com.habitatplus.app.ui.theme.*

@Composable
fun HistoryScreen(
    state: ParkingState = ParkingState(),
    onBackClick: () -> Unit = {},
    onDestinationClick: (HabitatDestination) -> Unit = {},
    onIntent: (ParkingIntent) -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme

    HabitatScaffold(
        title = "Historial de Parqueo",
        selectedDestination = HabitatDestination.PARKING,
        showBackButton = true,
        onBackClick = onBackClick,
        onDestinationClick = onDestinationClick
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            OutlinedTextField(
                value = state.historySearch,
                onValueChange = {
                    onIntent(ParkingIntent.HistorySearchChanged(it))
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Buscar por nombre, parqueo o fecha")
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_search),
                        contentDescription = null
                    )
                },
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                listOf(
                    "Todos",
                    "Activas",
                    "Finalizadas",
                    "Canceladas"
                ).forEach { filter ->
                    FilterChipDesign(
                        text = filter,
                        selected = state.historyFilter == filter,
                        onClick = {
                            onIntent(
                                ParkingIntent.HistoryFilterChanged(filter)
                            )
                        }
                    )
                }
            }

            Text(
                text = "Resumen de reservas",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.onBackground
            )

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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatisticItem(
                        number = state.totalReservations.toString(),
                        label = "Reservas\nregistradas"
                    )

                    VerticalDivider(
                        modifier = Modifier.height(42.dp),
                        color = colors.outlineVariant
                    )

                    StatisticItem(
                        number = state.totalVisitors.toString(),
                        label = "Visitantes\nregistrados"
                    )

                    VerticalDivider(
                        modifier = Modifier.height(42.dp),
                        color = colors.outlineVariant
                    )

                    StatisticItem(
                        number = state.mostUsedParking,
                        label = "Parqueo más\nutilizado"
                    )
                }
            }

            Text(
                text = "Actividad reciente",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.onBackground
            )

            if (state.error != null) {
                Text(
                    text = state.error,
                    color = colors.error,
                    fontSize = 12.sp
                )
            }

            if (state.filteredHistory.isEmpty()) {
                Text(
                    text = "No hay reservas para mostrar.",
                    color = colors.onSurfaceVariant,
                    fontSize = 12.sp
                )
            } else {
                state.filteredHistory.forEach { reservation ->
                    val isActive = reservation.status == "Activa"
                    val isCancelled = reservation.status == "Cancelada"

                    HistoryDesignCard(
                        name = reservation.visitorName,
                        plate = "No registrada",
                        parking = reservation.parkingId,
                        date = reservation.date,
                        time = reservation.time,
                        status = reservation.status,
                        statusColor = when {
                            isActive -> ParkingAvailable
                            isCancelled -> colors.onErrorContainer
                            else -> colors.onSecondaryContainer
                        },
                        statusBackground = when {
                            isActive -> ParkingAvailableContainer
                            isCancelled -> colors.errorContainer
                            else -> colors.secondaryContainer
                        },
                        onCancel = if (isActive) {
                            {
                                onIntent(
                                    ParkingIntent.CancelReservation(
                                        reservation.reservationId
                                    )
                                )
                            }
                        } else {
                            null
                        }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun FilterChipDesign(
    text: String,
    selected: Boolean = false,
    onClick: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme

    Surface(
        modifier = Modifier.clickable { onClick() },
        color = if (selected) {
            colors.primary
        } else {
            colors.surface
        },
        contentColor = if (selected) {
            colors.onPrimary
        } else {
            colors.onSurfaceVariant
        },
        shape = RoundedCornerShape(20.dp),
        tonalElevation = if (selected) 0.dp else 1.dp
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 7.dp
            ),
            color = if (selected) {
                colors.onPrimary
            } else {
                colors.onSurfaceVariant
            },
            fontSize = 10.sp,
            fontWeight = if (selected) {
                FontWeight.SemiBold
            } else {
                FontWeight.Normal
            }
        )
    }
}

@Composable
private fun StatisticItem(
    number: String,
    label: String
) {
    val colors = MaterialTheme.colorScheme

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = number,
            color = colors.primary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = label,
            color = colors.onSurfaceVariant,
            fontSize = 8.sp,
            lineHeight = 10.sp
        )
    }
}

@Composable
private fun HistoryDesignCard(
    name: String,
    plate: String,
    parking: String,
    date: String,
    time: String,
    status: String,
    statusColor: Color,
    statusBackground: Color,
    onCancel: (() -> Unit)? = null
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
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
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.onSurface
                    )

                    Text(
                        text = plate,
                        fontSize = 9.sp,
                        color = colors.onSurfaceVariant
                    )
                }

                Surface(
                    color = statusBackground,
                    contentColor = statusColor,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = status,
                        modifier = Modifier.padding(
                            horizontal = 9.dp,
                            vertical = 5.dp
                        ),
                        color = statusColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            HorizontalDivider(color = colors.outlineVariant)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "PARQUEO",
                        fontSize = 8.sp,
                        color = colors.onSurfaceVariant
                    )

                    Text(
                        text = parking,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.onSurface
                    )
                }

                Column {
                    Text(
                        text = "FECHA",
                        fontSize = 8.sp,
                        color = colors.onSurfaceVariant
                    )

                    Text(
                        text = date,
                        fontSize = 10.sp,
                        color = colors.onSurface
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "HORARIO",
                        fontSize = 8.sp,
                        color = colors.onSurfaceVariant
                    )

                    Text(
                        text = time,
                        fontSize = 10.sp,
                        color = colors.onSurface
                    )
                }
            }

            if (onCancel != null) {
                HorizontalDivider(color = colors.outlineVariant)

                val showConfirmation = remember {
                    mutableStateOf(false)
                }

                OutlinedButton(
                    onClick = {
                        showConfirmation.value = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = colors.error
                    )
                ) {
                    Text("Cancelar reserva")
                }

                if (showConfirmation.value) {
                    AlertDialog(
                        onDismissRequest = {
                            showConfirmation.value = false
                        },
                        title = {
                            Text("Cancelar reserva")
                        },
                        text = {
                            Text(
                                "¿Deseas cancelar la reserva del parqueo $parking?"
                            )
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    showConfirmation.value = false
                                    onCancel()
                                }
                            ) {
                                Text("Sí, cancelar")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    showConfirmation.value = false
                                }
                            ) {
                                Text("Volver")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Preview(
    name = "Historial - Claro",
    showBackground = true,
    widthDp = 411,
    heightDp = 891
)
@Composable
private fun HistoryScreenLightPreview() {
    ResidentTheme(darkTheme = false) {
        HistoryScreen()
    }
}

@Preview(
    name = "Historial - Oscuro",
    showBackground = true,
    widthDp = 411,
    heightDp = 891
)
@Composable
private fun HistoryScreenDarkPreview() {
    ResidentTheme(darkTheme = true) {
        HistoryScreen()
    }
}