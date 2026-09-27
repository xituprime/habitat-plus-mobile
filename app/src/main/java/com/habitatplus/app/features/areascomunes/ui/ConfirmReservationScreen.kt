package com.habitatplus.app.features.areascomunes.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitatplus.app.R
import com.habitatplus.app.features.areascomunes.intent.CommonAreaIntent
import com.habitatplus.app.features.areascomunes.state.CommonAreaState
import com.habitatplus.app.ui.theme.HabitatPlusTheme

@Composable
fun ConfirmReservationScreen(
    state: CommonAreaState,
    onIntent: (CommonAreaIntent) -> Unit = {},
    onBackClick: () -> Unit = {}
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            )
            .verticalScroll(
                rememberScrollState()
            )
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            )
    ) {

        // Encabezado interno de la pantalla.
        // El TopAppBar global lo agrega el equipo después.

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Surface(
                onClick = onBackClick,
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {

                Text(
                    text = "←",
                    modifier = Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 6.dp
                    ),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.padding(4.dp)
            )

            Text(
                text = "Confirmar Reserva",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.weight(1f)
            )

            Surface(
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {

                Text(
                    text = "Paso final",
                    modifier = Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 5.dp
                    ),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // Imagen de la cancha

        Image(
            painter = painterResource(
                id = R.drawable.area_court
            ),
            contentDescription = "Cancha polideportiva",
            modifier = Modifier
                .fillMaxWidth()
                .height(125.dp)
                .clip(
                    RoundedCornerShape(16.dp)
                ),
            contentScale = ContentScale.Crop
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        // Nombre del área sobre una franja visual

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.primary
        ) {

            Column(
                modifier = Modifier.padding(
                    horizontal = 12.dp,
                    vertical = 8.dp
                )
            ) {

                Text(
                    text = "ÁREA COMÚN",
                    fontSize = 8.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                Text(
                    text = "Cancha Polideportiva",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        // Datos de la reserva

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {

            Column(
                modifier = Modifier.padding(14.dp)
            ) {

                ReservationInfoRow(
                    label = "Área",
                    value = "Cancha"
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                ReservationInfoRow(
                    label = "Fecha",
                    value = state.selectedDate
                        ?: "15 de Septiembre"
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                ReservationInfoRow(
                    label = "Horario",
                    value = "${state.selectedTime ?: "18:00"} hrs",
                    secondaryValue = "Bloque nocturno"
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                ReservationInfoRow(
                    label = "Duración",
                    value = "1 hora fija",
                    secondaryValue =
                        "(${state.selectedTime ?: "18:00"} - ${
                            endTimeFor(
                                state.selectedTime ?: "18:00"
                            )
                        } hrs)"
                )

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                // Residente titular

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {

                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Surface(
                            shape = RoundedCornerShape(50),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {

                            Text(
                                text = "AP",
                                modifier = Modifier.padding(9.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(
                            modifier = Modifier.padding(5.dp)
                        )

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = "Residente Titular",
                                fontSize = 8.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                text = "Axel Perez",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "Apartamento\nA-203",
                                fontSize = 9.sp,
                                lineHeight = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Text(
                            text = "✓",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        // Normativa

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {

            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {

                Text(
                    text = "ⓘ",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(
                    modifier = Modifier.padding(4.dp)
                )

                Column {

                    Text(
                        text = "Normativa de uso",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Las reservas de áreas tienen una duración fija e improrrogable de 1 hora conforme al reglamento interno.",
                        fontSize = 9.sp,
                        lineHeight = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        // Mensaje previo

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 4.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "✓",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.padding(3.dp)
            )

            Text(
                text = "Comprobante digital autogenerado sin costo",
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        // Confirmar

        Button(
            onClick = {
                onIntent(
                    CommonAreaIntent.ConfirmReservation
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {

            Text(
                text = "Confirmar reserva",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )
    }
}

@Composable
private fun ReservationInfoRow(
    label: String,
    value: String,
    secondaryValue: String? = null
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = label,
            modifier = Modifier.weight(1f),
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Column(
            horizontalAlignment = Alignment.End
        ) {

            Text(
                text = value,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )

            if (secondaryValue != null) {

                Text(
                    text = secondaryValue,
                    fontSize = 8.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

private fun endTimeFor(
    startTime: String
): String {

    return when (startTime) {

        "09:00" -> "10:00"
        "10:00" -> "11:00"
        "11:00" -> "12:00"
        "12:00" -> "13:00"
        "18:00" -> "19:00"

        else -> "19:00"
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Confirmar Reserva - Teléfono"
)
@Composable
private fun ConfirmReservationPhonePreview() {

    HabitatPlusTheme {

        ConfirmReservationScreen(
            state = CommonAreaState(
                selectedAreaId = "court",
                selectedDate = "15 de Septiembre",
                selectedTime = "18:00"
            )
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = false,
    widthDp = 393,
    heightDp = 800,
    name = "Confirmar Reserva - Completo"
)
@Composable
private fun ConfirmReservationFullPreview() {

    HabitatPlusTheme {

        ConfirmReservationScreen(
            state = CommonAreaState(
                selectedAreaId = "court",
                selectedDate = "15 de Septiembre",
                selectedTime = "18:00"
            )
        )
    }
}