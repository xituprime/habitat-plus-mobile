package com.habitatplus.app.features.parqueos.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitatplus.app.features.parqueos.model.ParkingSpace
import com.habitatplus.app.features.parqueos.model.ParkingStatus
import com.habitatplus.app.ui.theme.HabitatBlue
import com.habitatplus.app.ui.theme.HabitatLightBlue
import com.habitatplus.app.ui.theme.HabitatSurface
import com.habitatplus.app.ui.theme.HabitatTextPrimary
import com.habitatplus.app.ui.theme.HabitatTextSecondary
import com.habitatplus.app.ui.theme.ParkingAvailable
import com.habitatplus.app.ui.theme.ParkingAvailableContainer
import com.habitatplus.app.ui.theme.ParkingMaintenance
import com.habitatplus.app.ui.theme.ParkingMaintenanceContainer
import com.habitatplus.app.ui.theme.ParkingOccupied
import com.habitatplus.app.ui.theme.ParkingOccupiedContainer

@Composable
fun VisitorParkingCard(
    parking: ParkingSpace,
    onReserveClick: () -> Unit = {}
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
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {
                    Text(
                        text = parking.id,
                        color = HabitatTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = when (parking.id) {
                            "V-01" -> "Nivel Calle"
                            "V-02" -> "Nivel Calle"
                            "V-03" -> "Sótano 1"
                            else -> "Parqueo de visita"
                        },
                        color = HabitatTextSecondary,
                        fontSize = 10.sp
                    )
                }

                ParkingBadge(
                    status = parking.status
                )
            }

            when (parking.status) {

                ParkingStatus.AVAILABLE -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Listo para asignación inmediata",
                            color = HabitatTextSecondary,
                            fontSize = 10.sp,
                            modifier = Modifier.weight(1f)
                        )

                        Button(
                            onClick = onReserveClick,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = HabitatBlue
                            )
                        ) {
                            Text(
                                text = "Reservar",
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                ParkingStatus.OCCUPIED -> {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = HabitatLightBlue.copy(alpha = 0.55f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "Vehículo registrado • Ingreso 08:30" +
                                    (parking.occupiedUntil?.let {
                                        " • Hasta $it"
                                    } ?: ""),
                            modifier = Modifier.padding(10.dp),
                            color = HabitatTextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }

                ParkingStatus.MAINTENANCE -> {
                    Text(
                        text = "Mantenimiento preventivo programado",
                        color = HabitatTextSecondary,
                        fontSize = 10.sp
                    )
                }

                ParkingStatus.ASSIGNED -> Unit
            }
        }
    }
}

@Composable
private fun ParkingBadge(
    status: ParkingStatus
) {
    val text: String
    val foreground: Color
    val background: Color

    when (status) {
        ParkingStatus.AVAILABLE -> {
            text = "● Disponible"
            foreground = ParkingAvailable
            background = ParkingAvailableContainer
        }

        ParkingStatus.OCCUPIED -> {
            text = "● Ocupado"
            foreground = ParkingOccupied
            background = ParkingOccupiedContainer
        }

        ParkingStatus.MAINTENANCE -> {
            text = "⚒ Mantenimiento"
            foreground = ParkingMaintenance
            background = ParkingMaintenanceContainer
        }

        ParkingStatus.ASSIGNED -> {
            text = "Asignado"
            foreground = HabitatBlue
            background = HabitatLightBlue
        }
    }

    Surface(
        color = background,
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = 8.dp,
                vertical = 4.dp
            ),
            color = foreground,
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium
        )
    }
}