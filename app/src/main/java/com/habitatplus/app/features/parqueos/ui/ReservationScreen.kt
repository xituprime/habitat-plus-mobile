
package com.habitatplus.app.features.parqueos.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitatplus.app.R
import com.habitatplus.app.features.parqueos.intent.ParkingIntent
import com.habitatplus.app.features.parqueos.state.ParkingState
import com.habitatplus.app.ui.components.HabitatDestination
import com.habitatplus.app.ui.components.HabitatScaffold
import com.habitatplus.app.ui.theme.ParkingAvailable
import com.habitatplus.app.ui.theme.ParkingAvailableContainer
import com.habitatplus.app.ui.theme.ResidentTheme
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationScreen(
    state: ParkingState = ParkingState(),
    onIntent: (ParkingIntent) -> Unit = {},
    onBackClick: () -> Unit = {},
    onReservationSuccess: () -> Unit = {},
    onDestinationClick: (HabitatDestination) -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme

    var showDatePicker by remember { mutableStateOf(false) }
    var showEntryTimePicker by remember { mutableStateOf(false) }
    var showExitTimePicker by remember { mutableStateOf(false) }

    LaunchedEffect(state.reservationSuccess) {
        if (state.reservationSuccess) {
            onReservationSuccess()
        }
    }

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
                    containerColor = colors.primaryContainer,
                    contentColor = colors.onPrimaryContainer
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
                            text = state.reservationForm.parkingId,
                            color = colors.onPrimaryContainer,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Nivel Calle",
                            color = colors.onPrimaryContainer,
                            fontSize = 10.sp
                        )
                    }

                    Surface(
                        color = ParkingAvailableContainer,
                        contentColor = ParkingAvailable,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "Disponible",
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

            FormSectionTitle("Datos de la visita")

            HabitatField(
                value = state.reservationForm.visitorName,
                label = "Nombre del visitante",
                onValueChange = {
                    onIntent(ParkingIntent.VisitorNameChanged(it))
                },
                isError = state.reservationForm.visitorNameError != null,
                errorMessage = state.reservationForm.visitorNameError
            )

            HabitatField(
                value = state.reservationForm.apartment,
                label = "Apartamento",
                supporting = "Asignado automáticamente",
                readOnly = true
            )

            FormSectionTitle("Datos del vehículo")

            HabitatField(
                value = state.reservationForm.licensePlate,
                label = "Placa",
                onValueChange = {
                    onIntent(ParkingIntent.LicensePlateChanged(it))
                },
                isError = state.reservationForm.licensePlateError != null,
                errorMessage = state.reservationForm.licensePlateError
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    HabitatField(
                        value = state.reservationForm.vehicleBrand,
                        label = "Marca",
                        onValueChange = {
                            onIntent(
                                ParkingIntent.VehicleBrandChanged(it)
                            )
                        }
                    )
                }

                Box(modifier = Modifier.weight(1f)) {
                    HabitatField(
                        value = state.reservationForm.vehicleColor,
                        label = "Color",
                        onValueChange = {
                            onIntent(
                                ParkingIntent.VehicleColorChanged(it)
                            )
                        }
                    )
                }
            }

            FormSectionTitle("Fotografía del vehículo")

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colors.surface,
                    contentColor = colors.onSurface
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
                        painter = painterResource(R.drawable.ic_camera),
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(30.dp)
                    )

                    Text(
                        text = "Adjuntar foto del vehículo",
                        color = colors.onSurface,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "Opcional",
                        fontSize = 9.sp,
                        color = colors.onSurfaceVariant
                    )

                    // Integración de cámara pendiente.
                    OutlinedButton(
                        onClick = {},
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = colors.primary
                        )
                    ) {
                        Text(
                            text = "Tomar fotografía",
                            fontSize = 10.sp
                        )
                    }
                }
            }

            FormSectionTitle("Fecha y horario")

            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (state.reservationForm.date.isBlank()) {
                        "Seleccionar fecha"
                    } else {
                        "Fecha: ${state.reservationForm.date}"
                    }
                )
            }

            state.reservationForm.dateError?.let {
                Text(
                    text = it,
                    color = colors.error,
                    fontSize = 11.sp
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedButton(
                        onClick = { showEntryTimePicker = true },
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(
                            horizontal = 4.dp,
                            vertical = 12.dp
                        )
                    ) {
                        Text(
                            text = state.reservationForm.entryTime
                                .ifBlank { "Hora ingreso" },
                            fontSize = 12.sp
                        )
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    OutlinedButton(
                        onClick = { showExitTimePicker = true },
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(
                            horizontal = 4.dp,
                            vertical = 12.dp
                        )
                    ) {
                        Text(
                            text = state.reservationForm.exitTime
                                .ifBlank { "Hora salida" },
                            fontSize = 12.sp
                        )
                    }
                }
            }

            state.reservationForm.entryTimeError?.let {
                Text(
                    text = it,
                    color = colors.error,
                    fontSize = 11.sp
                )
            }

            state.reservationForm.exitTimeError?.let {
                Text(
                    text = it,
                    color = colors.error,
                    fontSize = 11.sp
                )
            }

            Text(
                text = "Los horarios se seleccionan en intervalos de 30 minutos.",
                fontSize = 11.sp,
                color = colors.onSurfaceVariant
            )

            state.error?.let { message ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = colors.errorContainer,
                    contentColor = colors.onErrorContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = message,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (state.isSavingReservation) {
                Text(
                    text = "Guardando reserva…",
                    color = colors.onBackground
                )
            }

            if (state.reservationSuccess) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = colors.secondaryContainer,
                    contentColor = colors.onSecondaryContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Reserva realizada correctamente.",
                        modifier = Modifier.padding(12.dp),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Button(
                onClick = {
                    onIntent(ParkingIntent.SubmitReservation)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary,
                    contentColor = colors.onPrimary
                ),
                enabled = !state.isSavingReservation
            ) {
                Text(
                    text = "Confirmar Reserva de Parqueo",
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(12.dp))
        }
    }

    if (showDatePicker) {
        ParkingDatePickerDialog(
            currentDate = state.reservationForm.date,
            onDismiss = { showDatePicker = false },
            onConfirm = { date ->
                onIntent(ParkingIntent.DateChanged(date))
                showDatePicker = false
            }
        )
    }

    if (showEntryTimePicker) {
        ParkingTimePickerDialog(
            title = "Hora de ingreso",
            currentTime = state.reservationForm.entryTime,
            onDismiss = { showEntryTimePicker = false },
            onConfirm = { time ->
                onIntent(ParkingIntent.EntryTimeChanged(time))
                showEntryTimePicker = false
            }
        )
    }

    if (showExitTimePicker) {
        ParkingTimePickerDialog(
            title = "Hora de salida",
            currentTime = state.reservationForm.exitTime,
            onDismiss = { showExitTimePicker = false },
            onConfirm = { time ->
                onIntent(ParkingIntent.ExitTimeChanged(time))
                showExitTimePicker = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ParkingDatePickerDialog(
    currentDate: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val initialDate = remember(currentDate) {
        runCatching { LocalDate.parse(currentDate) }
            .getOrNull()
            ?: LocalDate.now()
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate
            .atStartOfDay()
            .toInstant(ZoneOffset.UTC)
            .toEpochMilli(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(
                utcTimeMillis: Long
            ): Boolean {
                val date = Instant.ofEpochMilli(utcTimeMillis)
                    .atZone(ZoneOffset.UTC)
                    .toLocalDate()

                return !date.isBefore(LocalDate.now())
            }
        }
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val millis = datePickerState.selectedDateMillis
                    if (millis != null) {
                        val date = Instant.ofEpochMilli(millis)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()

                        if (!date.isBefore(LocalDate.now())) {
                            onConfirm(date.toString())
                        }
                    }
                },
                enabled = datePickerState.selectedDateMillis != null
            ) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ParkingTimePickerDialog(
    title: String,
    currentTime: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val initialTime = remember(currentTime) {
        runCatching {
            LocalTime.parse(currentTime)
        }.getOrNull() ?: LocalTime.of(8, 0)
    }

    val timePickerState = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(title)
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                TimePicker(state = timePickerState)

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "La hora se ajustará al intervalo de 30 minutos más cercano.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val totalMinutes =
                        timePickerState.hour * 60 +
                                timePickerState.minute

                    // Redondear a intervalos de 30 minutos.
                    val roundedMinutes =
                        ((totalMinutes + 15) / 30) * 30

                    val hour = (roundedMinutes / 60) % 24
                    val minute = roundedMinutes % 60

                    val formattedTime = String.format(
                        java.util.Locale.US,
                        "%02d:%02d",
                        hour,
                        minute
                    )

                    onConfirm(formattedTime)
                }
            ) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun HabitatField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit = {},
    supporting: String? = null,
    readOnly: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        supportingText = when {
            errorMessage != null -> {
                { Text(errorMessage) }
            }
            supporting != null -> {
                {
                    Text(
                        text = supporting,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            else -> null
        },
        modifier = Modifier.fillMaxWidth(),
        readOnly = readOnly,
        isError = isError,
        singleLine = true,
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
private fun FormSectionTitle(title: String) {
    Text(
        text = title,
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(top = 4.dp)
    )
}

@Preview(
    name = "Reservar Parqueo - Claro",
    showBackground = true,
    widthDp = 411,
    heightDp = 891
)
@Composable
private fun ReservationScreenLightPreview() {
    ResidentTheme(darkTheme = false) {
        ReservationScreen()
    }
}

@Preview(
    name = "Reservar Parqueo - Oscuro",
    showBackground = true,
    widthDp = 411,
    heightDp = 891
)
@Composable
private fun ReservationScreenDarkPreview() {
    ResidentTheme(darkTheme = true) {
        ReservationScreen()
    }
}
