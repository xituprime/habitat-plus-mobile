package com.habitatplus.app.features.parqueos.ui

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
import com.habitatplus.app.ui.components.HabitatDestination
import com.habitatplus.app.ui.components.HabitatScaffold
import com.habitatplus.app.ui.theme.*

@Composable
fun ParkingScreen() {

    HabitatScaffold(
        title = "Parqueos",
        selectedDestination = HabitatDestination.PARKING
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
                            color = HabitatTextPrimary
                        )

                        Text(
                            text = "Apartamento A-203",
                            fontSize = 11.sp,
                            color = HabitatTextSecondary
                        )
                    }

                    StatusChip(
                        text = "● Activo",
                        color = ParkingAvailable,
                        background = ParkingAvailableContainer
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = HabitatLightBlue
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
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        SmallLabel("PLACA ASIGNADA")

                        Surface(
                            color = HabitatLightBlue,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "HAB-892",
                                modifier = Modifier.padding(
                                    horizontal = 10.dp,
                                    vertical = 5.dp
                                ),
                                color = HabitatBlue,
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
                    text = "2 disponibles",
                    color = ParkingAvailable,
                    background = ParkingAvailableContainer
                )
            }

            VisitorCard(
                code = "V-01",
                location = "Nivel Calle",
                status = "Disponible",
                statusColor = ParkingAvailable,
                statusBackground = ParkingAvailableContainer,
                message = "Listo para asignación inmediata",
                showReserve = true
            )

            VisitorCard(
                code = "V-02",
                location = "Nivel Calle",
                status = "Ocupado",
                statusColor = ParkingOccupied,
                statusBackground = ParkingOccupiedContainer,
                message = "Carlos Méndez • Hasta las 18:00"
            )

            VisitorCard(
                code = "V-03",
                location = "Sótano 1",
                status = "Mantenimiento",
                statusColor = ParkingMaintenance,
                statusBackground = ParkingMaintenanceContainer,
                message = "Mantenimiento preventivo programado"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                SectionTitle("Últimas Reservas")

                Text(
                    text = "Ver historial",
                    color = HabitatBlue,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            ReservationSummaryCard(
                name = "Carlos Pérez",
                parking = "V-02",
                date = "Hoy • 17:00"
            )

            ReservationSummaryCard(
                name = "Andrea López",
                parking = "V-01",
                date = "Ayer • 14:30"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {},
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HabitatBlue
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
    code: String,
    location: String,
    status: String,
    statusColor: Color,
    statusBackground: Color,
    message: String,
    showReserve: Boolean = false
) {

    ParkingCard {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column {
                Text(
                    text = code,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = location,
                    fontSize = 10.sp,
                    color = HabitatTextSecondary
                )
            }

            StatusChip(
                text = "● $status",
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
                    color = HabitatTextSecondary
                )

                Button(
                    onClick = {},
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(
                        horizontal = 14.dp,
                        vertical = 4.dp
                    ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HabitatBlue
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
                color = HabitatBackground,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = message,
                    modifier = Modifier.padding(10.dp),
                    fontSize = 10.sp,
                    color = HabitatTextSecondary
                )
            }
        }
    }
}

@Composable
private fun ReservationSummaryCard(
    name: String,
    parking: String,
    date: String
) {

    ParkingCard {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column {
                Text(
                    text = name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "Parqueo $parking",
                    fontSize = 9.sp,
                    color = HabitatTextSecondary
                )

                Text(
                    text = date,
                    fontSize = 9.sp,
                    color = HabitatTextSecondary
                )
            }

            StatusChip(
                text = "Finalizada",
                color = HabitatSecondary,
                background = HabitatLightBlue
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
            containerColor = HabitatSurface
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
        color = HabitatTextPrimary,
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
        color = HabitatTextSecondary,
        fontSize = 8.sp
    )
}

@Preview(
    name = "01 - Parqueos",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp,dpi=420"
)
@Composable
private fun ParkingScreenPreview() {
    HabitatPlusTheme {
        ParkingScreen()
    }
}