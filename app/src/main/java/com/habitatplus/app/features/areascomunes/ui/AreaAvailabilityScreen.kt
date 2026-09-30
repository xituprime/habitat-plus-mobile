package com.habitatplus.app.features.areascomunes.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitatplus.app.features.areascomunes.intent.CommonAreaIntent
import com.habitatplus.app.features.areascomunes.model.TimeSlot
import com.habitatplus.app.features.areascomunes.model.TimeSlotStatus
import com.habitatplus.app.features.areascomunes.state.CommonAreaState
import com.habitatplus.app.ui.theme.HabitatPlusTheme

@Composable
fun AreaAvailabilityScreen(
    state: CommonAreaState,
    onIntent: (CommonAreaIntent) -> Unit = {},
    onContinueClick: () -> Unit = {}
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

        // Título

        Text(
            text = "RESERVA COMUNITARIA",
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Disponibilidad de\nCancha",
                fontSize = 22.sp,
                lineHeight = 22.sp,
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
                    text = "Cancha",
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 6.dp
                    ),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        // Mes

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Septiembre 2024",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.weight(1f)
            )

            Surface(
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {

                Text(
                    text = "Semana 38",
                    modifier = Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 5.dp
                    ),
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        // Selector de días

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(
                    rememberScrollState()
                ),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            DateOption(
                day = "Mié",
                number = "13",
                selected = false,
                onClick = {
                    onIntent(
                        CommonAreaIntent.SelectDate(
                            "13 de Septiembre"
                        )
                    )
                }
            )

            DateOption(
                day = "Jue",
                number = "14",
                selected = false,
                onClick = {
                    onIntent(
                        CommonAreaIntent.SelectDate(
                            "14 de Septiembre"
                        )
                    )
                }
            )

            DateOption(
                day = "Vie",
                number = "15",
                selected =
                    state.selectedDate == "15 de Septiembre",
                onClick = {
                    onIntent(
                        CommonAreaIntent.SelectDate(
                            "15 de Septiembre"
                        )
                    )
                }
            )

            DateOption(
                day = "Sáb",
                number = "16",
                selected = false,
                onClick = {
                    onIntent(
                        CommonAreaIntent.SelectDate(
                            "16 de Septiembre"
                        )
                    )
                }
            )

            DateOption(
                day = "Dom",
                number = "17",
                selected = false,
                onClick = {
                    onIntent(
                        CommonAreaIntent.SelectDate(
                            "17 de Septiembre"
                        )
                    )
                }
            )
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        // Agenda

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Agenda diaria de Cancha",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Turnos exactos de 1 hora",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "Viernes, 15 de Septiembre • Iluminación incluida",
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {

                Text(
                    text = "60 min",
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
            modifier = Modifier.height(10.dp)
        )

        // Horarios

        state.timeSlots.forEach { slot ->

            TimeSlotCard(
                slot = slot,
                selected =
                    state.selectedTime == slot.startTime,
                onClick = {

                    if (slot.status == TimeSlotStatus.AVAILABLE) {

                        onIntent(
                            CommonAreaIntent.SelectTime(
                                slot.startTime
                            )
                        )
                    }
                }
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )
        }

        // Información

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {

            Text(
                text = "Por reglamento condominal, la reserva es individual de 1 hora exacta sin costo adicional.",
                modifier = Modifier.padding(12.dp),
                fontSize = 10.sp,
                lineHeight = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // Continuar

        Button(
            onClick = onContinueClick,
            enabled = state.selectedTime != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {

            Text(
                text = if (state.selectedTime != null) {
                    "Continuar con horario ${state.selectedTime}"
                } else {
                    "Selecciona un horario"
                },
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
private fun DateOption(
    day: String,
    number: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    Surface(
        modifier = Modifier
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(12.dp),
        color = if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        }
    ) {

        Column(
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = day,
                fontSize = 9.sp,
                color = if (selected) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )

            Text(
                text = number,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (selected) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}

@Composable
private fun TimeSlotCard(
    slot: TimeSlot,
    selected: Boolean,
    onClick: () -> Unit
) {

    val isAvailable =
        slot.status == TimeSlotStatus.AVAILABLE

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                enabled = isAvailable
            ) {
                onClick()
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Hora grande izquierda

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            ) {

                Text(
                    text = slot.startTime,
                    modifier = Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 8.dp
                    ),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selected) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            }

            Spacer(
                modifier = Modifier.padding(4.dp)
            )

            // Información

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "${slot.startTime} - ${slot.endTime}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = slot.description,
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = if (isAvailable) {
                        "Disponible"
                    } else {
                        "Reservado"
                    },
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isAvailable) {
                        MaterialTheme.colorScheme.tertiary
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                )
            }

            // Acción

            Surface(
                shape = RoundedCornerShape(50),
                color = when {

                    selected ->
                        MaterialTheme.colorScheme.primary

                    isAvailable ->
                        MaterialTheme.colorScheme.primaryContainer

                    else ->
                        MaterialTheme.colorScheme.surfaceVariant
                }
            ) {

                Text(
                    text = when {

                        selected ->
                            "Elegido"

                        isAvailable ->
                            "Seleccionar"

                        else ->
                            "No disponible"
                    },
                    modifier = Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 6.dp
                    ),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = when {

                        selected ->
                            MaterialTheme.colorScheme.onPrimary

                        isAvailable ->
                            MaterialTheme.colorScheme.primary

                        else ->
                            MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Disponibilidad - Teléfono"
)
@Composable
private fun AreaAvailabilityPhonePreview() {

    HabitatPlusTheme {

        AreaAvailabilityScreen(
            state = availabilityPreviewState()
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = false,
    widthDp = 393,
    heightDp = 1000,
    name = "Disponibilidad - Vista Completa"
)
@Composable
private fun AreaAvailabilityFullPreview() {

    HabitatPlusTheme {

        AreaAvailabilityScreen(
            state = availabilityPreviewState()
        )
    }
}

private fun availabilityPreviewState(): CommonAreaState {

    return CommonAreaState(
        selectedAreaId = "court",
        selectedDate = "15 de Septiembre",
        selectedTime = "18:00",

        timeSlots = listOf(

            TimeSlot(
                startTime = "09:00",
                endTime = "10:00",
                description = "Turno matutino • Cancha de tenis",
                status = TimeSlotStatus.AVAILABLE
            ),

            TimeSlot(
                startTime = "10:00",
                endTime = "11:00",
                description = "Ocupado por depto B-104",
                status = TimeSlotStatus.RESERVED
            ),

            TimeSlot(
                startTime = "11:00",
                endTime = "12:00",
                description = "Turno mediodía",
                status = TimeSlotStatus.AVAILABLE
            ),

            TimeSlot(
                startTime = "12:00",
                endTime = "13:00",
                description = "Turno soleado",
                status = TimeSlotStatus.AVAILABLE
            ),

            TimeSlot(
                startTime = "18:00",
                endTime = "19:00",
                description = "Turno vespertino • Con reflectores",
                status = TimeSlotStatus.AVAILABLE
            )
        )
    )
}