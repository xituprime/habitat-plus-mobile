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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitatplus.app.R
import com.habitatplus.app.ui.components.HabitatDestination
import com.habitatplus.app.ui.components.HabitatScaffold
import com.habitatplus.app.ui.theme.*

@Composable
fun HistoryScreen(
    onBackClick: () -> Unit = {},
    onDestinationClick: (HabitatDestination) -> Unit = {}
) {

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
                value = "",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                placeholder = {
                    Text(
                        text = "Buscar por nombre, placa o fecha"
                    )
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(
                            R.drawable.ic_search
                        ),
                        contentDescription = null
                    )
                },
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {

                FilterChipDesign(
                    text = "Todos",
                    selected = true
                )

                FilterChipDesign("Activas")
                FilterChipDesign("Finalizadas")
                FilterChipDesign("Canceladas")
            }

            Text(
                text = "Resumen de Septiembre",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = HabitatTextPrimary
            )

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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    StatisticItem(
                        number = "15",
                        label = "Reservas\neste mes"
                    )

                    VerticalDivider(
                        modifier = Modifier.height(42.dp),
                        color = HabitatLightBlue
                    )

                    StatisticItem(
                        number = "24",
                        label = "Visitantes\nregistrados"
                    )

                    VerticalDivider(
                        modifier = Modifier.height(42.dp),
                        color = HabitatLightBlue
                    )

                    StatisticItem(
                        number = "V-02",
                        label = "Parqueo más\nutilizado"
                    )
                }
            }

            Text(
                text = "Actividad reciente",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = HabitatTextPrimary
            )

            HistoryDesignCard(
                name = "Carlos Pérez",
                plate = "P-123ABC",
                parking = "V-02",
                date = "26 Septiembre",
                time = "14:00 - 16:00",
                status = "Activa",
                statusColor = ParkingAvailable,
                statusBackground = ParkingAvailableContainer
            )

            HistoryDesignCard(
                name = "Andrea López",
                plate = "P-842KLM",
                parking = "V-01",
                date = "25 Septiembre",
                time = "10:00 - 12:00",
                status = "Finalizada",
                statusColor = HabitatSecondary,
                statusBackground = HabitatLightBlue
            )

            HistoryDesignCard(
                name = "Luis Morales",
                plate = "P-927XYZ",
                parking = "V-03",
                date = "23 Septiembre",
                time = "17:00 - 19:00",
                status = "Cancelada",
                statusColor = ParkingOccupied,
                statusBackground = ParkingOccupiedContainer
            )

            HistoryDesignCard(
                name = "María García",
                plate = "P-456DEF",
                parking = "V-02",
                date = "20 Septiembre",
                time = "08:00 - 10:00",
                status = "Finalizada",
                statusColor = HabitatSecondary,
                statusBackground = HabitatLightBlue
            )

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun FilterChipDesign(
    text: String,
    selected: Boolean = false
) {

    Surface(
        color = if (selected) {
            HabitatBlue
        } else {
            HabitatSurface
        },
        shape = RoundedCornerShape(20.dp),
        tonalElevation = if (selected) {
            0.dp
        } else {
            1.dp
        }
    ) {

        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 7.dp
            ),
            color = if (selected) {
                Color.White
            } else {
                HabitatTextSecondary
            },
            fontSize = 9.sp,
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

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = number,
            color = HabitatBlue,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = label,
            color = HabitatTextSecondary,
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
    statusBackground: Color
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Column {

                    Text(
                        text = name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = plate,
                        fontSize = 9.sp,
                        color = HabitatTextSecondary
                    )
                }

                Surface(
                    color = statusBackground,
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

            HorizontalDivider(
                color = HabitatLightBlue
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Column {
                    Text(
                        text = "PARQUEO",
                        fontSize = 8.sp,
                        color = HabitatTextSecondary
                    )

                    Text(
                        text = parking,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Column {
                    Text(
                        text = "FECHA",
                        fontSize = 8.sp,
                        color = HabitatTextSecondary
                    )

                    Text(
                        text = date,
                        fontSize = 10.sp
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "HORARIO",
                        fontSize = 8.sp,
                        color = HabitatTextSecondary
                    )

                    Text(
                        text = time,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Preview(
    name = "03 - Historial y Estadísticas",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp,dpi=420"
)
@Composable
private fun HistoryScreenPreview() {
    HabitatPlusTheme {
        HistoryScreen()
    }
}