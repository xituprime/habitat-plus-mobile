package com.habitatplus.app.features.parqueos.ui

import android.service.autofill.OnClickAction
import android.widget.Space
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.habitatplus.app.features.parqueos.model.ParkingSpace
import androidx.compose.material3.Text
import com.habitatplus.app.features.parqueos.model.ParkingStatus
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.habitatplus.app.ui.theme.HabitatPlusTheme

@Composable
fun VisitorParkingCard(
    parking: ParkingSpace,
    onReserveClick: () -> Unit = {}
){

    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = parking.id,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Muestra el estado visualmente
            ParkingStatusIndicator(
                status = parking.status
            )

            Spacer(modifier = Modifier.height(12.dp))


            when (parking.status) {

                ParkingStatus.AVAILABLE -> {
                    Button(
                        onClick = onReserveClick
                    ) {
                        Text("Reservar")
                    }
                }

                ParkingStatus.OCCUPIED -> {
                    parking.occupiedUntil?.let {
                        Text(
                            text = "Hasta las $it"
                        )
                    }
                }

                ParkingStatus.MAINTENANCE -> Unit

                ParkingStatus.ASSIGNED -> Unit
            }
        }
    }
}
@Composable
private fun ParkingStatusIndicator(
    status: ParkingStatus
) {
    val statusText: String
    val statusColor: Color

    when (status) {
        ParkingStatus.AVAILABLE -> {
            statusText = "Disponible"
            statusColor = Color(0xFF4CAF50)
        }

        ParkingStatus.OCCUPIED -> {
            statusText = "Ocupado"
            statusColor = Color(0xFFF44336)
        }

        ParkingStatus.MAINTENANCE -> {
            statusText = "Mantenimiento"
            statusColor = Color(0xFFFF9800)
        }

        ParkingStatus.ASSIGNED -> {
            statusText = "Asignado"
            statusColor = MaterialTheme.colorScheme.primary
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(
                    color = statusColor,
                    shape = CircleShape
                )
        )

        Spacer(
            modifier = Modifier.width(8.dp)
        )

        Text(
            text = statusText,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
