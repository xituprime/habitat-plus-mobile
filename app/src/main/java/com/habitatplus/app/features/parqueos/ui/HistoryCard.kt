package com.habitatplus.app.features.parqueos.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.habitatplus.app.features.parqueos.model.ParkingHistory

@Composable
fun HistoryCard(
    history: ParkingHistory
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
                text = history.visitorName,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = history.parkingId
            )

            Text(
                text = "${history.date} • ${history.time}"
            )

            Text(
                text = history.status,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}