
package com.habitatplus.app.features.parqueos.ui

import android.app.DatePickerDialog
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
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
import com.habitatplus.app.ui.theme.HabitatPlusTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.ZoneId
import java.util.Locale

@Composable
fun ReservationScreen(
    state: ParkingState = ParkingState(),
    onIntent: (ParkingIntent) -> Unit = {},
    onBackClick: () -> Unit = {},
    onReservationSuccess: () -> Unit = {},
    onVehiclePhotoChanged: (Bitmap?) -> Unit = {},
    onDestinationClick: (HabitatDestination) -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(state.reservationSuccess) {
        if (state.reservationSuccess) {
            onReservationSuccess()
        }
    }

    // Fotografia temporal del vehiculo.
    var vehiclePhoto by remember {
        mutableStateOf<Bitmap?>(null)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            vehiclePhoto = bitmap
            onVehiclePhotoChanged(bitmap)
        }
    }

    // Selector de fecha.
    val datePicker = remember(context) {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                val selectedDate = LocalDate.of(
                    year,
                    month + 1,
                    day
                )

                onIntent(
                    ParkingIntent.DateChanged(
                        selectedDate.format(
                            DateTimeFormatter.ISO_LOCAL_DATE
                        )
                    )
                )
            },
            LocalDate.now().year,
            LocalDate.now().monthValue - 1,
            LocalDate.now().dayOfMonth
        ).apply {
            datePicker.minDate = LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        }
    }

    // Horarios en intervalos exactos de 30 minutos.
    val timeSlots = remember {
        (0 until 48).map { index ->
            val hour = index / 2
            val minute = if (index % 2 == 0) 0 else 30

            String.format(
                Locale.US,
                "%02d:%02d",
                hour,
                minute
            )
        }
    }

    var selectingTime by remember {
        mutableStateOf<String?>(null)
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
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            // Parqueo seleccionado.
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
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Parqueo para visitantes",
                            fontSize = 10.sp
                        )
                    }

                    Surface(
                        color = colors.secondaryContainer,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "Seleccionado",
                            modifier = Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 5.dp
                            ),
                            color = colors.onSecondaryContainer,
                            fontSize = 10.sp
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
                isError =
                    state.reservationForm.visitorNameError != null,
                errorMessage =
                    state.reservationForm.visitorNameError
            )

            HabitatField(
                value = state.reservationForm.apartment,
                label = "Apartamento",
                supporting = "Asignado automaticamente",
                readOnly = true
            )

            FormSectionTitle("Datos del vehiculo")

            HabitatField(
                value = state.reservationForm.licensePlate,
                label = "Placa",
                onValueChange = {
                    onIntent(ParkingIntent.LicensePlateChanged(it))
                },
                isError =
                    state.reservationForm.licensePlateError != null,
                errorMessage =
                    state.reservationForm.licensePlateError
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(Modifier.weight(1f)) {
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

                Box(Modifier.weight(1f)) {
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

            // Camara del vehiculo.
            FormSectionTitle("Fotografia del vehiculo")

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colors.surface,
                    contentColor = colors.onSurface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (vehiclePhoto != null) {
                        Image(
                            bitmap = vehiclePhoto!!.asImageBitmap(),
                            contentDescription =
                                "Fotografia del vehiculo",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(170.dp),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            painter = painterResource(
                                R.drawable.ic_camera
                            ),
                            contentDescription = null,
                            tint = colors.primary,
                            modifier = Modifier.size(30.dp)
                        )

                        Text(
                            text = "Adjuntar foto del vehiculo",
                            color = colors.onSurface,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = "Opcional",
                            color = colors.onSurfaceVariant,
                            fontSize = 10.sp
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            cameraLauncher.launch(null)
                        },
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = if (vehiclePhoto == null) {
                                "Tomar fotografia"
                            } else {
                                "Cambiar fotografia"
                            }
                        )
                    }
                }
            }

            // Fecha y horario.
            FormSectionTitle("Fecha y horario")

            OutlinedCard(
                onClick = {
                    datePicker.show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        text = "Fecha de reserva",
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.onSurfaceVariant
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = state.reservationForm.date.ifBlank {
                            "Seleccionar fecha"
                        },
                        color = colors.onSurface
                    )

                    state.reservationForm.dateError?.let {
                        Text(it, color = colors.error)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(Modifier.weight(1f)) {
                    OutlinedCard(
                        onClick = {
                            selectingTime = "entry"
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text(
                                "Hora ingreso",
                                style = MaterialTheme.typography.labelSmall
                            )

                            Spacer(Modifier.height(4.dp))

                            Text(
                                state.reservationForm.entryTime.ifBlank {
                                    "Seleccionar"
                                }
                            )
                        }
                    }
                }

                Box(Modifier.weight(1f)) {
                    OutlinedCard(
                        onClick = {
                            selectingTime = "exit"
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text(
                                "Hora salida",
                                style = MaterialTheme.typography.labelSmall
                            )

                            Spacer(Modifier.height(4.dp))

                            Text(
                                state.reservationForm.exitTime.ifBlank {
                                    "Seleccionar"
                                }
                            )
                        }
                    }
                }
            }

            state.reservationForm.entryTimeError?.let {
                Text(it, color = colors.error)
            }

            state.reservationForm.exitTimeError?.let {
                Text(it, color = colors.error)
            }

            state.error?.let {
                Text(
                    text = it,
                    color = colors.error
                )
            }

            Button(
                onClick = {
                    onIntent(ParkingIntent.SubmitReservation)
                },
                enabled = !state.isSavingReservation,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                if (state.isSavingReservation) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = colors.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "Confirmar reserva de parqueo",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
        }
    }

    // Dialogo de seleccion de horarios.
    if (selectingTime != null) {
        val isEntry = selectingTime == "entry"

        AlertDialog(
            onDismissRequest = {
                selectingTime = null
            },
            title = {
                Text(
                    if (isEntry) {
                        "Seleccionar hora de ingreso"
                    } else {
                        "Seleccionar hora de salida"
                    }
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .heightIn(max = 350.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    timeSlots.forEach { time ->
                        TextButton(
                            onClick = {
                                if (isEntry) {
                                    onIntent(
                                        ParkingIntent.EntryTimeChanged(
                                            time
                                        )
                                    )
                                } else {
                                    onIntent(
                                        ParkingIntent.ExitTimeChanged(
                                            time
                                        )
                                    )
                                }

                                selectingTime = null
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(time)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectingTime = null
                    }
                ) {
                    Text("Cerrar")
                }
            }
        )
    }
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
    val colors = MaterialTheme.colorScheme

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        supportingText = when {
            errorMessage != null -> {
                { Text(errorMessage) }
            }
            supporting != null -> {
                { Text(supporting, fontSize = 9.sp) }
            }
            else -> null
        },
        modifier = Modifier.fillMaxWidth(),
        readOnly = readOnly,
        isError = isError,
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = colors.onSurface,
            unfocusedTextColor = colors.onSurface,
            focusedContainerColor = colors.surface,
            unfocusedContainerColor = colors.surface,
            focusedBorderColor = colors.primary,
            unfocusedBorderColor = colors.outline,
            cursorColor = colors.primary
        ),
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

@Preview(name = "Reserva clara", showBackground = true)
@Composable
private fun ReservationLightPreview() {
    HabitatPlusTheme(darkTheme = false) {
        ReservationScreen()
    }
}

@Preview(name = "Reserva oscura", showBackground = true)
@Composable
private fun ReservationDarkPreview() {
    HabitatPlusTheme(darkTheme = true) {
        ReservationScreen()
    }
}
