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
        ){
            Text(
                text = parking.id,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            when (parking.status){

                ParkingStatus.AVAILABLE -> {
                    Text(
                        text = "🟢 Disponible"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onReserveClick
                    ) {

                        Text("Reservar")

                    }
                }

                ParkingStatus.OCCUPIED -> {

                    Text("🔴 Ocupado")

                    parking.occupiedUntil?.let {

                        Text(
                            text = "hasta las $it"
                        )
                    }
                }

                ParkingStatus.MAINTENANCE -> {

                    Text("🟠 Mantenimiento")
                }

                ParkingStatus.ASSIGNDED -> Unit
            }
        }
    }
}
