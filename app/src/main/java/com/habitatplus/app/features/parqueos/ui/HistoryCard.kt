package com.habitatplus.app.features.parqueos.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.habitatplus.app.features.parqueos.model.ParkingHistory
import com.habitatplus.app.ui.theme.HabitatBlue
import com.habitatplus.app.ui.theme.HabitatBlueContainer
import com.habitatplus.app.ui.theme.HabitatTextSecondary

@Composable
fun HistoryCard(
    history: ParkingHistory
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = history.visitorName,
                    style = MaterialTheme.typography.titleSmall
                )

                Surface(
                    color = HabitatBlueContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = history.status,
                        modifier = Modifier.padding(
                            horizontal = 8.dp,
                            vertical = 4.dp
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = HabitatBlue
                    )
                }
            }

            Text(
                text = "Parqueo ${history.parkingId}",
                style = MaterialTheme.typography.bodySmall,
                color = HabitatTextSecondary
            )

            Text(
                text = "${history.date} • ${history.time}",
                style = MaterialTheme.typography.bodySmall,
                color = HabitatTextSecondary
            )
        }
    }
}