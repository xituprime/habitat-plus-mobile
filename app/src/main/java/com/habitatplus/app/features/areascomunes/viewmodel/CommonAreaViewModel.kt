package com.habitatplus.app.features.areascomunes.viewmodel

import androidx.lifecycle.ViewModel
import com.habitatplus.app.features.areascomunes.intent.CommonAreaIntent
import com.habitatplus.app.features.areascomunes.model.CommonArea
import com.habitatplus.app.features.areascomunes.model.CommonAreaStatus
import com.habitatplus.app.features.areascomunes.model.TimeSlot
import com.habitatplus.app.features.areascomunes.model.TimeSlotStatus
import com.habitatplus.app.features.areascomunes.state.CommonAreaState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CommonAreaViewModel : ViewModel() {

    private val initialAreas = listOf(
        CommonArea(
            id = "social-room",
            name = "Salón social",
            description = "Capacidad 50 personas, equipado con mobiliario, cocina auxiliar y climatización.",
            locationType = "Interior",
            availabilityText = "Hasta 50 personas",
            features = listOf("Climatizado", "Cocina"),
            status = CommonAreaStatus.AVAILABLE
        ),
        CommonArea(
            id = "court",
            name = "Cancha",
            description = "Cancha polideportiva sintética iluminada, disponible por turnos de 1 hora.",
            locationType = "Exterior",
            availabilityText = "Turnos de 1 hora",
            features = listOf("Iluminación LED", "Césped sintético"),
            status = CommonAreaStatus.AVAILABLE
        ),
        CommonArea(
            id = "pool",
            name = "Piscina",
            description = "Área acuática y solárium, normas de higiene vigentes.",
            locationType = "Solárium",
            availabilityText = "Normas vigentes",
            features = listOf("Solárium", "Duchas previas"),
            status = CommonAreaStatus.AVAILABLE
        ),
        CommonArea(
            id = "grill",
            name = "Churrasquera",
            description = "Parrilla techada con mesas exteriores y lavadero.",
            locationType = "Techado",
            availabilityText = "Parrilla & Mesas",
            features = listOf("Techado", "Lavadero"),
            status = CommonAreaStatus.AVAILABLE
        )
    )

    private val demoReservations = setOf(
        Triple("court", "15 de Septiembre", "10:00"),
        Triple("pool", "15 de Septiembre", "12:00"),
        Triple("social-room", "16 de Septiembre", "18:00"),
        Triple("grill", "17 de Septiembre", "11:00")
    )

    private val userReservations =
        mutableSetOf<Triple<String, String, String>>()

    private val validDates = listOf(
        "13 de Septiembre",
        "14 de Septiembre",
        "15 de Septiembre",
        "16 de Septiembre",
        "17 de Septiembre"
    )

    private val _state = MutableStateFlow(
        CommonAreaState(areas = initialAreas)
    )

    val state: StateFlow<CommonAreaState> =
        _state.asStateFlow()

    fun onIntent(intent: CommonAreaIntent) {
        when (intent) {
            CommonAreaIntent.LoadAreas -> loadAreas()

            CommonAreaIntent.LoadAvailability -> {
                loadAvailability()
            }

            is CommonAreaIntent.SelectArea -> {
                selectArea(intent.areaId)
            }

            is CommonAreaIntent.SelectDate -> {
                selectDate(intent.date)
            }

            is CommonAreaIntent.SelectTime -> {
                selectTime(intent.time)
            }

            CommonAreaIntent.ConfirmReservation -> {
                confirmReservation()
            }
        }
    }

    private fun loadAreas() {
        _state.update {
            it.copy(
                isLoading = false,
                areas = initialAreas
            )
        }
    }

    private fun selectArea(areaId: String) {
        val selectedArea = initialAreas.firstOrNull {
            it.id == areaId
        } ?: return

        if (selectedArea.status != CommonAreaStatus.AVAILABLE) {
            return
        }

        _state.update {
            it.copy(
                selectedAreaId = areaId,
                selectedDate = null,
                selectedTime = null,
                timeSlots = emptyList(),
                isReservationConfirmed = false
            )
        }
    }

    private fun selectDate(date: String) {
        val current = _state.value
        val areaId = current.selectedAreaId ?: return

        if (date !in validDates) return

        _state.update {
            it.copy(
                selectedDate = date,
                selectedTime = null,
                isReservationConfirmed = false,
                timeSlots = createTimeSlots(areaId, date)
            )
        }
    }

    private fun loadAvailability() {
        val current = _state.value
        val areaId = current.selectedAreaId ?: return

        val date = current.selectedDate
            ?.takeIf { it in validDates }
            ?: "15 de Septiembre"

        _state.update {
            it.copy(
                isLoading = false,
                selectedDate = date,
                selectedTime = null,
                timeSlots = createTimeSlots(areaId, date),
                isReservationConfirmed = false
            )
        }
    }

    private fun createTimeSlots(
        areaId: String,
        date: String
    ): List<TimeSlot> {

        val schedules = listOf(
            "09:00" to "10:00",
            "10:00" to "11:00",
            "11:00" to "12:00",
            "12:00" to "13:00",
            "18:00" to "19:00"
        )

        return schedules.map { (start, end) ->
            val key = Triple(areaId, date, start)

            val reserved =
                key in demoReservations ||
                        key in userReservations

            val description = when {
                reserved &&
                        areaId == "court" &&
                        date == "15 de Septiembre" &&
                        start == "10:00" ->
                    "Ocupado por depto B-104"

                reserved ->
                    "Horario ocupado"

                start == "09:00" ->
                    "Turno matutino"

                start == "11:00" ->
                    "Turno mediodía"

                start == "12:00" ->
                    "Turno soleado"

                start == "18:00" && areaId == "court" ->
                    "Turno vespertino • Con reflectores"

                start == "18:00" ->
                    "Turno vespertino"

                else ->
                    "Turno disponible de 1 hora"
            }

            TimeSlot(
                startTime = start,
                endTime = end,
                description = description,
                status = if (reserved) {
                    TimeSlotStatus.RESERVED
                } else {
                    TimeSlotStatus.AVAILABLE
                }
            )
        }
    }

    private fun selectTime(time: String) {
        val current = _state.value

        if (
            current.selectedAreaId == null ||
            current.selectedDate == null ||
            current.isReservationConfirmed
        ) return

        val selectedSlot = current.timeSlots.firstOrNull {
            it.startTime == time
        }

        if (selectedSlot?.status != TimeSlotStatus.AVAILABLE) {
            return
        }

        _state.update {
            it.copy(
                selectedTime = time,
                isReservationConfirmed = false
            )
        }
    }

    private fun confirmReservation() {
        val current = _state.value

        // Evitar confirmar dos veces
        if (current.isReservationConfirmed) return

        val areaId = current.selectedAreaId ?: return
        val date = current.selectedDate ?: return
        val time = current.selectedTime ?: return

        val slot = current.timeSlots.firstOrNull {
            it.startTime == time
        }

        if (slot?.status != TimeSlotStatus.AVAILABLE) {
            return
        }

        val key = Triple(areaId, date, time)

        if (
            key in demoReservations ||
            key in userReservations
        ) return

        userReservations.add(key)

        _state.update {
            it.copy(
                timeSlots = createTimeSlots(areaId, date),
                selectedTime = time,
                selectedDate = date,
                selectedAreaId = areaId,
                isReservationConfirmed = true
            )
        }
    }
}