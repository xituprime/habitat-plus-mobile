package com.habitatplus.app.features.parqueos.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.habitatplus.app.features.parqueos.model.ParkingSpace
import androidx.compose.material3.Text

@Composable
fun ResidentParkingCard(
    parking: ParkingSpace
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

            parking.apartment?.let {

                Text(
                    text = "Apartamento $it",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text(
                text = "Asignado",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}