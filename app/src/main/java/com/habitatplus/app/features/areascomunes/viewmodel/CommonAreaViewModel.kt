package com.habitatplus.app.features.areascomunes.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.habitatplus.app.data.local.CommonAreaReservationEntity
import com.habitatplus.app.data.local.DatabaseProvider
import com.habitatplus.app.data.repository.CommonAreaReservationRepository
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
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit

class CommonAreaViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = CommonAreaReservationRepository(
        DatabaseProvider
            .getDatabase(application)
            .commonAreaReservationDao()
    )

    private val initialAreas = listOf(
        CommonArea(
            id = "social-room",
            name = "Salón social",
            description = "Capacidad 50 personas, equipado con mobiliario, cocina auxiliar y climatización.",
            locationType = "Interior",
            availabilityText = "Hasta 50 personas",
            features = listOf(
                "Climatizado",
                "Cocina"
            ),
            status = CommonAreaStatus.AVAILABLE
        ),
        CommonArea(
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
        ),
        CommonArea(
            id = "pool",
            name = "Piscina",
            description = "Área acuática y solárium, normas de higiene vigentes.",
            locationType = "Solárium",
            availabilityText = "Normas vigentes",
            features = listOf(
                "Solárium",
                "Duchas previas"
            ),
            status = CommonAreaStatus.AVAILABLE
        ),
        CommonArea(
            id = "grill",
            name = "Churrasquera",
            description = "Parrilla techada con mesas exteriores y lavadero.",
            locationType = "Techado",
            availabilityText = "Parrilla & Mesas",
            features = listOf(
                "Techado",
                "Lavadero"
            ),
            status = CommonAreaStatus.AVAILABLE
        )
    )

    // Reservas de demostración.
    // Siguen siendo datos falsos y no se guardan en Room.
    private val demoReservations = setOf(
        Triple("court", 0L, "10:00"),
        Triple("pool", 1L, "12:00"),
        Triple("social-room", 2L, "18:00"),
        Triple("grill", 3L, "11:00")
    )

    // Copia local de las reservas leídas desde Room.
    private var savedReservations:
            Set<Triple<String, String, String>> = emptySet()

    // No permitimos seleccionar horarios hasta que
    // Room haya terminado la primera lectura.
    private var reservationsLoaded = false

    private val _state = MutableStateFlow(
        CommonAreaState(
            isLoading = true,
            areas = initialAreas
        )
    )

    val state: StateFlow<CommonAreaState> =
        _state.asStateFlow()

    init {
        observeSavedReservations()
    }

    fun onIntent(intent: CommonAreaIntent) {
        when (intent) {

            CommonAreaIntent.LoadAreas -> {
                loadAreas()
            }

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

    private fun observeSavedReservations() {
        viewModelScope.launch {
            try {
                repository.reservations.collect { reservations ->

                    savedReservations = reservations.map {
                        Triple(
                            it.areaId,
                            it.date,
                            it.startTime
                        )
                    }.toSet()

                    reservationsLoaded = true

                    val current = _state.value

                    val areaId = current.selectedAreaId
                    val date = parseValidDate(current.selectedDate)

                    _state.update {
                        it.copy(
                            isLoading = false,
                            timeSlots = if (
                                areaId != null &&
                                date != null
                            ) {
                                createTimeSlots(areaId, date)
                            } else {
                                it.timeSlots
                            }
                        )
                    }
                }
            } catch (exception: Exception) {
                reservationsLoaded = false

                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage =
                            "No se pudieron cargar las reservas locales."
                    )
                }
            }
        }
    }

    private fun isValidDate(date: LocalDate): Boolean {
        val today = LocalDate.now()

        return !date.isBefore(today) &&
                !date.isAfter(today.plusDays(30))
    }

    private fun parseValidDate(value: String?): LocalDate? {
        if (value == null) return null

        return try {
            LocalDate.parse(value).takeIf(::isValidDate)
        } catch (exception: DateTimeParseException) {
            null
        }
    }

    private fun loadAreas() {
        _state.update {
            it.copy(
                areas = initialAreas,
                isLoading = !reservationsLoaded
            )
        }
    }

    private fun selectArea(areaId: String) {
        val area = initialAreas.firstOrNull {
            it.id == areaId
        } ?: return

        if (area.status != CommonAreaStatus.AVAILABLE) {
            return
        }

        _state.update {
            it.copy(
                selectedAreaId = areaId,
                selectedDate = null,
                selectedTime = null,
                timeSlots = emptyList(),
                isReservationConfirmed = false,
                isSavingReservation = false,
                errorMessage = null
            )
        }
    }

    private fun loadAvailability() {
        val current = _state.value
        val areaId = current.selectedAreaId ?: return

        val date = parseValidDate(current.selectedDate)
            ?: LocalDate.now()

        _state.update {
            it.copy(
                isLoading = !reservationsLoaded,
                selectedDate = date.toString(),
                selectedTime = null,
                timeSlots = if (reservationsLoaded) {
                    createTimeSlots(areaId, date)
                } else {
                    emptyList()
                },
                isReservationConfirmed = false,
                errorMessage = null
            )
        }
    }

    private fun selectDate(date: String) {
        val current = _state.value
        val areaId = current.selectedAreaId ?: return
        val parsedDate = parseValidDate(date) ?: return

        _state.update {
            it.copy(
                selectedDate = parsedDate.toString(),
                selectedTime = null,
                timeSlots = if (reservationsLoaded) {
                    createTimeSlots(areaId, parsedDate)
                } else {
                    emptyList()
                },
                isReservationConfirmed = false,
                errorMessage = null
            )
        }
    }

    private fun isDemoReserved(
        areaId: String,
        date: LocalDate,
        time: String
    ): Boolean {
        val offset = ChronoUnit.DAYS.between(
            LocalDate.now(),
            date
        )

        return Triple(areaId, offset, time) in demoReservations
    }

    private fun isReserved(
        areaId: String,
        date: LocalDate,
        time: String
    ): Boolean {
        return isDemoReserved(areaId, date, time) ||
                Triple(
                    areaId,
                    date.toString(),
                    time
                ) in savedReservations
    }

    private fun createTimeSlots(
        areaId: String,
        date: LocalDate
    ): List<TimeSlot> {

        val schedules = listOf(
            "09:00" to "10:00",
            "10:00" to "11:00",
            "11:00" to "12:00",
            "12:00" to "13:00",
            "18:00" to "19:00"
        )

        return schedules.map { (start, end) ->

            val reserved = isReserved(areaId, date, start)

            val pastTime = date == LocalDate.now() &&
                    !LocalTime.parse(start).isAfter(
                        LocalTime.now()
                    )

            val unavailable = reserved || pastTime

            val description = when {
                reserved -> "Horario ocupado"
                pastTime -> "Horario ya transcurrido"
                start == "09:00" -> "Turno matutino"
                start == "11:00" -> "Turno mediodía"
                start == "12:00" -> "Turno soleado"
                start == "18:00" && areaId == "court" ->
                    "Turno vespertino • Con reflectores"
                start == "18:00" -> "Turno vespertino"
                else -> "Turno disponible de 1 hora"
            }

            TimeSlot(
                startTime = start,
                endTime = end,
                description = description,
                status = if (unavailable) {
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
            !reservationsLoaded ||
            current.isLoading ||
            current.isSavingReservation ||
            current.isReservationConfirmed
        ) return

        val areaId = current.selectedAreaId ?: return
        val date = parseValidDate(current.selectedDate) ?: return

        val slot = createTimeSlots(areaId, date)
            .firstOrNull {
                it.startTime == time
            }

        if (slot?.status != TimeSlotStatus.AVAILABLE) {
            return
        }

        _state.update {
            it.copy(
                selectedTime = time,
                timeSlots = createTimeSlots(areaId, date),
                errorMessage = null
            )
        }
    }

    private fun confirmReservation() {
        val current = _state.value

        if (
            !reservationsLoaded ||
            current.isLoading ||
            current.isSavingReservation ||
            current.isReservationConfirmed
        ) return

        val areaId = current.selectedAreaId ?: return
        val date = parseValidDate(current.selectedDate) ?: return
        val time = current.selectedTime ?: return

        val slot = createTimeSlots(areaId, date)
            .firstOrNull {
                it.startTime == time
            }

        if (slot?.status != TimeSlotStatus.AVAILABLE) {
            return
        }

        _state.update {
            it.copy(
                isSavingReservation = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            try {
                // Room tiene un índice único que impide
                // duplicar área + fecha + horario.

                repository.saveReservation(
                    CommonAreaReservationEntity(
                        areaId = areaId,
                        date = date.toString(),
                        startTime = time,
                        endTime = slot.endTime
                    )
                )

                // Solo después de guardar correctamente
                // confirmamos la reserva en MVI.

                savedReservations = savedReservations +
                        Triple(areaId, date.toString(), time)

                _state.update {
                    it.copy(
                        isSavingReservation = false,
                        isReservationConfirmed = true,
                        selectedAreaId = areaId,
                        selectedDate = date.toString(),
                        selectedTime = time,
                        timeSlots = createTimeSlots(areaId, date),
                        errorMessage = null
                    )
                }

            } catch (exception: Exception) {
                _state.update {
                    it.copy(
                        isSavingReservation = false,
                        isReservationConfirmed = false,
                        errorMessage =
                            "No se pudo guardar la reserva localmente."
                    )
                }
            }
        }
    }
}
