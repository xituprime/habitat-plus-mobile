package com.habitatplus.app.features.parqueos.ui

import androidx.compose.foundation.background
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
fun ReservationScreen(
    onBackClick: () -> Unit = {},
    onDestinationClick: (HabitatDestination) -> Unit = {}
) {

    HabitatScaffold(
        title = "Reservar Parqueo",
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Spacer(Modifier.height(4.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = HabitatLightBlue
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column {
                        Text(
                            text = "V-01",
                            color = HabitatBlue,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Nivel Calle",
                            color = HabitatTextSecondary,
                            fontSize = 10.sp
                        )
                    }

                    Surface(
                        color = ParkingAvailableContainer,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "● Disponible",
                            modifier = Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 5.dp
                            ),
                            color = ParkingAvailable,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            FormSectionTitle(
                title = "Datos de la visita"
            )

            HabitatField(
                value = "Carlos Pérez",
                label = "Nombre del visitante"
            )

            HabitatField(
                value = "A-203",
                label = "Apartamento",
                supporting = "Asignado automáticamente"
            )

            FormSectionTitle(
                title = "Datos del vehículo"
            )

            HabitatField(
                value = "P-123ABC",
                label = "Placa"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    HabitatField(
                        value = "Toyota",
                        label = "Marca"
                    )
                }

                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    HabitatField(
                        value = "Blanco",
                        label = "Color"
                    )
                }
            }

            FormSectionTitle(
                title = "Fotografía del vehículo"
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

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Icon(
                        painter = painterResource(
                            R.drawable.ic_camera
                        ),
                        contentDescription = null,
                        tint = HabitatBlue,
                        modifier = Modifier.size(30.dp)
                    )

                    Text(
                        text = "Adjuntar foto del vehículo",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "Opcional",
                        fontSize = 9.sp,
                        color = HabitatTextSecondary
                    )

                    OutlinedButton(
                        onClick = {},
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "Tomar fotografía",
                            fontSize = 10.sp
                        )
                    }
                }
            }

            FormSectionTitle(
                title = "Fecha y horario"
            )

            HabitatField(
                value = "26 Septiembre 2026",
                label = "Fecha"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    HabitatField(
                        value = "14:00",
                        label = "Hora ingreso"
                    )
                }

                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    HabitatField(
                        value = "16:00",
                        label = "Hora salida"
                    )
                }
            }

            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HabitatBlue
                )
            ) {
                Text(
                    text = "Confirmar Reserva de Parqueo",
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun HabitatField(
    value: String,
    label: String,
    supporting: String? = null
) {

    OutlinedTextField(
        value = value,
        onValueChange = {},
        label = {
            Text(label)
        },
        supportingText = supporting?.let {
            {
                Text(
                    text = it,
                    fontSize = 9.sp
                )
            }
        },
        modifier = Modifier.fillMaxWidth(),
        readOnly = true,
        singleLine = true,
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
private fun FormSectionTitle(
    title: String
) {

    Text(
        text = title,
        color = HabitatTextPrimary,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(top = 4.dp)
    )
}

@Preview(
    name = "02 - Reservar Parqueo",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp,dpi=420"
)
@Composable
private fun ReservationScreenPreview() {
    HabitatPlusTheme {
        ReservationScreen()
    }
}