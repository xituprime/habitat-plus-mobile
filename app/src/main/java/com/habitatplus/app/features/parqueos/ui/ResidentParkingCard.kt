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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitatplus.app.features.parqueos.model.ParkingSpace
import com.habitatplus.app.ui.theme.HabitatBlue
import com.habitatplus.app.ui.theme.HabitatLightBlue
import com.habitatplus.app.ui.theme.HabitatSurface
import com.habitatplus.app.ui.theme.HabitatTextPrimary
import com.habitatplus.app.ui.theme.HabitatTextSecondary
import com.habitatplus.app.ui.theme.ParkingAvailable
import com.habitatplus.app.ui.theme.ParkingAvailableContainer

@Composable
fun ResidentParkingCard(
    parking: ParkingSpace
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
                        text = parking.id,
                        color = HabitatTextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    parking.apartment?.let {
                        Text(
                            text = "Apartamento $it",
                            color = HabitatTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Surface(
                    color = ParkingAvailableContainer,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "● Activo",
                        modifier = Modifier.padding(
                            horizontal = 9.dp,
                            vertical = 5.dp
                        ),
                        color = ParkingAvailable,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Ubicación",
                        color = HabitatTextSecondary,
                        fontSize = 9.sp
                    )

                    Text(
                        text = "Sótano 1",
                        color = HabitatTextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Column {
                    Text(
                        text = "PLACA ASIGNADA",
                        color = HabitatTextSecondary,
                        fontSize = 8.sp
                    )

                    Surface(
                        color = HabitatLightBlue,
                        shape = RoundedCornerShape(7.dp)
                    ) {
                        Text(
                            text = "HAB-892",
                            modifier = Modifier.padding(
                                horizontal = 8.dp,
                                vertical = 4.dp
                            ),
                            color = HabitatBlue,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}