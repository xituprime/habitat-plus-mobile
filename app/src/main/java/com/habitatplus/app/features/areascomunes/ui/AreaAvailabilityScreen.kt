package com.habitatplus.app.features.areascomunes.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitatplus.app.features.areascomunes.intent.CommonAreaIntent
import com.habitatplus.app.features.areascomunes.model.CommonArea
import com.habitatplus.app.features.areascomunes.model.CommonAreaStatus
import com.habitatplus.app.features.areascomunes.model.TimeSlot
import com.habitatplus.app.features.areascomunes.model.TimeSlotStatus
import com.habitatplus.app.features.areascomunes.state.CommonAreaState
import com.habitatplus.app.ui.components.HabitatBackHeader
import com.habitatplus.app.ui.theme.HabitatPlusTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale

@Composable
fun AreaAvailabilityScreen(
    state: CommonAreaState,
    onIntent: (CommonAreaIntent) -> Unit = {},
    onContinueClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {

    val selectedArea = state.areas.firstOrNull {
        it.id == state.selectedAreaId
    }

    val areaName = selectedArea?.name ?: "Área común"

    val areaInformation = when (state.selectedAreaId) {
        "court" -> "Iluminación incluida"
        "social-room" -> "Espacio climatizado"
        "pool" -> "Normas de higiene vigentes"
        "grill" -> "Parrilla y mesas disponibles"
        else -> "Reserva comunitaria"
    }

    val locale = Locale("es", "GT")

    val today = remember {
        LocalDate.now()
    }

    val maxDate = today.plusDays(30)

    val firstSunday = remember(today) {
        today.with(
            TemporalAdjusters.previousOrSame(
                DayOfWeek.SUNDAY
            )
        )
    }

    val lastSunday = remember(today) {
        maxDate.with(
            TemporalAdjusters.previousOrSame(
                DayOfWeek.SUNDAY
            )
        )
    }

    val maxPage = remember(today) {
        java.time.temporal.ChronoUnit.WEEKS
            .between(firstSunday, lastSunday)
            .toInt()
    }

    // Estado únicamente visual:
    // controla qué semana se muestra.
    var page by remember {
        mutableIntStateOf(0)
    }

    val weekStart = firstSunday.plusWeeks(page.toLong())

    val visibleDates = remember(weekStart) {
        (0L..6L).map {
            weekStart.plusDays(it)
        }
    }

    val selectedDate = state.selectedDate?.let {
        runCatching {
            LocalDate.parse(it)
        }.getOrNull()
    }

    val dateFormatter = DateTimeFormatter.ofPattern(
        "EEEE, d 'de' MMMM 'de' yyyy",
        locale
    )

    val displayedDate = selectedDate?.format(
        dateFormatter
    ) ?: "Selecciona una fecha"

    val monthFormatter = DateTimeFormatter.ofPattern(
        "MMMM yyyy",
        locale
    )

    val monthTitle = weekStart.format(
        monthFormatter
    ).replaceFirstChar {
        it.titlecase(locale)
    }

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

        // Encabezado original de Axel

        HabitatBackHeader(
            title = "Regresar a áreas comunes",
            onBackClick = onBackClick
        )

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
                text = "Disponibilidad de\n$areaName",
                fontSize = 22.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            Surface(
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {

                Text(
                    text = areaName,
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

        // Mes y navegación entre semanas

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = monthTitle,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            Surface(
                onClick = {
                    if (page > 0) {
                        page--
                    }
                },
                enabled = page > 0,
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {

                WeekArrow(
                    forward = false,
                    color = if (page > 0) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }

            Spacer(
                modifier = Modifier.width(6.dp)
            )

            Surface(
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {

                Text(
                    text = "Semana ${page + 1}",
                    modifier = Modifier.padding(
                        horizontal = 9.dp,
                        vertical = 6.dp
                    ),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(
                modifier = Modifier.width(6.dp)
            )

            Surface(
                onClick = {
                    if (page < maxPage) {
                        page++
                    }
                },
                enabled = page < maxPage,
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {

                WeekArrow(
                    forward = true,
                    color = if (page < maxPage) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        // Calendario semanal: domingo a sábado

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            visibleDates.forEach { date ->

                val isEnabled =
                    !date.isBefore(today) &&
                            !date.isAfter(maxDate)

                val dayName = date.dayOfWeek
                    .getDisplayName(
                        TextStyle.SHORT,
                        locale
                    )
                    .replace(".", "")
                    .replaceFirstChar {
                        it.titlecase(locale)
                    }

                DateOption(
                    day = dayName,
                    number = date.dayOfMonth.toString(),
                    selected = selectedDate == date,
                    enabled = isEnabled,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onIntent(
                            CommonAreaIntent.SelectDate(
                                date.toString()
                            )
                        )
                    }
                )
            }
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        // Agenda diaria original

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Agenda diaria de $areaName",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Turnos exactos de 1 hora",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "$displayedDate • $areaInformation",
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

        // Tarjetas originales de horarios

        state.timeSlots.forEach { slot ->

            TimeSlotCard(
                slot = slot,
                selected =
                    state.selectedTime == slot.startTime &&
                            !state.isReservationConfirmed,
                onClick = {

                    if (
                        slot.status ==
                        TimeSlotStatus.AVAILABLE
                    ) {

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

        // Información reglamentaria original

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

        // Botón Continuar original

        Button(
            onClick = onContinueClick,
            enabled =
                state.selectedAreaId != null &&
                        state.selectedDate != null &&
                        state.selectedTime != null &&
                        !state.isReservationConfirmed &&
                        state.timeSlots.any {
                            it.startTime == state.selectedTime &&
                                    it.status == TimeSlotStatus.AVAILABLE
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
                text = if (state.isReservationConfirmed) {
                    "Horario ya reservado"
                } else if (state.selectedTime != null) {
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
private fun WeekArrow(
    forward: Boolean,
    color: Color
) {
    Box(
        modifier = Modifier
            .size(width = 38.dp, height = 30.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(18.dp)) {
            val tipX = if (forward) size.width * 0.72f else size.width * 0.28f
            val baseX = if (forward) size.width * 0.28f else size.width * 0.72f
            drawLine(
                color = color,
                start = Offset(baseX, size.height * 0.2f),
                end = Offset(tipX, size.height * 0.5f),
                strokeWidth = 2.5.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawLine(
                color = color,
                start = Offset(tipX, size.height * 0.5f),
                end = Offset(baseX, size.height * 0.8f),
                strokeWidth = 2.5.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
private fun DateOption(
    day: String,
    number: String,
    selected: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Surface(
        modifier = modifier.clickable(
            enabled = enabled,
            onClick = onClick
        ),
        shape = RoundedCornerShape(12.dp),
        color = when {
            selected ->
                MaterialTheme.colorScheme.primary

            enabled ->
                MaterialTheme.colorScheme.surfaceVariant

            else ->
                MaterialTheme.colorScheme.surfaceVariant.copy(
                    alpha = 0.45f
                )
        }
    ) {

        Column(
            modifier = Modifier.padding(
                horizontal = 2.dp,
                vertical = 8.dp
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = day,
                fontSize = 9.sp,
                maxLines = 1,
                color = when {
                    selected ->
                        MaterialTheme.colorScheme.onPrimary

                    enabled ->
                        MaterialTheme.colorScheme.onSurfaceVariant

                    else ->
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(
                            alpha = 0.45f
                        )
                }
            )

            Text(
                text = number,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = when {
                    selected ->
                        MaterialTheme.colorScheme.onPrimary

                    enabled ->
                        MaterialTheme.colorScheme.onSurface

                    else ->
                        MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.45f
                        )
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
                enabled = isAvailable,
                onClick = onClick
            ),
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

            // Hora izquierda

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
                modifier = Modifier.width(8.dp)
            )

            // Información del turno

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
                        "No disponible"
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
                        selected -> "Elegido"
                        isAvailable -> "Seleccionar"
                        else -> "No disponible"
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

    val area = CommonArea(
        id = "court",
        name = "Cancha",
        description = "Cancha polideportiva sintética iluminada, disponible por turnos de 1 hora.",
        locationType = "Exterior",
        availabilityText = "Turnos de 1 hora",
        features = listOf(
            "Iluminación LED",
            "Césped sintético"
        ),
        status = CommonAreaStatus.AVAILABLE
    )

    val previewDate = LocalDate.now().plusDays(1)

    return CommonAreaState(
        areas = listOf(area),
        selectedAreaId = "court",
        selectedDate = previewDate.toString(),
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