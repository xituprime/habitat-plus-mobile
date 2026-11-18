package com.habitatplus.app.features.areascomunes.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.habitatplus.app.data.repository.CloudCommonAreaReservation
import com.habitatplus.app.data.repository.CommonAreaFirestoreRepository
import com.habitatplus.app.features.areascomunes.intent.CommonAreaIntent
import com.habitatplus.app.features.areascomunes.model.CommonArea
import com.habitatplus.app.features.areascomunes.model.CommonAreaStatus
import com.habitatplus.app.features.areascomunes.model.TimeSlot
import com.habitatplus.app.features.areascomunes.model.TimeSlotStatus
import com.habitatplus.app.features.areascomunes.state.CommonAreaState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeParseException

class CommonAreaViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val auth = FirebaseAuth.getInstance()

    private val repository =
        CommonAreaFirestoreRepository()

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
            features = listOf("Techado", "Lavadero"),
            status = CommonAreaStatus.AVAILABLE
        )
    )

    private val schedules = listOf(
        "09:00" to "10:00",
        "10:00" to "11:00",
        "11:00" to "12:00",
        "12:00" to "13:00",
        "18:00" to "19:00"
    )

    private var savedReservations:
            List<CloudCommonAreaReservation> = emptyList()

    private var reservationsLoaded = false
    private var observerJob: Job? = null
    private var observedUid: String? = null

    private val _state = MutableStateFlow(
        CommonAreaState(
            isLoading = true,
            areas = initialAreas
        )
    )

    val state: StateFlow<CommonAreaState> =
        _state.asStateFlow()

    init {
        observeReservations()
    }

    fun onIntent(intent: CommonAreaIntent) {
        when (intent) {

            CommonAreaIntent.LoadAreas -> {
                observeReservations()
                loadAreas()
            }

            CommonAreaIntent.LoadAvailability -> {
                observeReservations()
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

    private fun observeReservations() {
        val uid = auth.currentUser?.uid

        if (uid == null) {
            observerJob?.cancel()
            observerJob = null
            observedUid = null
            reservationsLoaded = false
            savedReservations = emptyList()

            _state.update {
                it.copy(
                    isLoading = false,
                    timeSlots = emptyList(),
                    selectedTime = null,
                    errorMessage = "Debes iniciar sesión."
                )
            }
            return
        }

        if (
            observerJob?.isActive == true &&
            observedUid == uid
        ) return

        observerJob?.cancel()

        observedUid = uid
        reservationsLoaded = false
        savedReservations = emptyList()

        _state.update {
            it.copy(
                isLoading = true,
                timeSlots = emptyList(),
                selectedTime = null,
                isReservationConfirmed = false,
                errorMessage = null
            )
        }

        observerJob = viewModelScope.launch {
            try {
                repository.observeReservations()
                    .collect { snapshot ->

                        if (auth.currentUser?.uid != uid) {
                            return@collect
                        }

                        // No tratamos datos de caché como
                        // disponibilidad confirmada.
                        reservationsLoaded =
                            !snapshot.isFromCache

                        savedReservations =
                            snapshot.reservations

                        refreshSlots()

                    }
            } catch (exception: Exception) {
                Log.e(
                    "HabitatAreas",
                    "Error al consultar reservas",
                    exception
                )

                reservationsLoaded = false

                _state.update {
                    it.copy(
                        isLoading = false,
                        selectedTime = null,
                        timeSlots = emptyList(),
                        errorMessage =
                            "No se pudo consultar la disponibilidad en Firestore."
                    )
                }
            }
        }
    }

    private fun refreshSlots() {
        val current = _state.value
        val areaId = current.selectedAreaId
        val date = parseValidDate(current.selectedDate)

        val slots = if (
            reservationsLoaded &&
            areaId != null &&
            date != null
        ) {
            createTimeSlots(areaId, date)
        } else {
            emptyList()
        }

        val selectedStillAvailable =
            slots.any {
                it.startTime == current.selectedTime &&
                        it.status == TimeSlotStatus.AVAILABLE
            }

        _state.update {
            it.copy(
                isLoading = !reservationsLoaded,
                timeSlots = slots,
                selectedTime = if (
                    it.isReservationConfirmed ||
                    selectedStillAvailable
                ) {
                    it.selectedTime
                } else {
                    null
                },
                errorMessage = if (reservationsLoaded) {
                    null
                } else {
                    "Esperando disponibilidad actualizada del servidor."
                }
            )
        }
    }

    private fun isValidDate(date: LocalDate): Boolean {
        val today = LocalDate.now()

        return !date.isBefore(today) &&
                !date.isAfter(today.plusDays(30))
    }

    private fun parseValidDate(
        value: String?
    ): LocalDate? {
        if (value == null) return null

        return try {
            LocalDate.parse(value)
                .takeIf(::isValidDate)
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

        val date = parseValidDate(
            current.selectedDate
        ) ?: LocalDate.now()

        _state.update {
            it.copy(
                selectedDate = date.toString(),
                selectedTime = null,
                isReservationConfirmed = false,
                errorMessage = null
            )
        }

        refreshSlots()
    }

    private fun selectDate(date: String) {
        val areaId = _state.value.selectedAreaId ?: return
        val parsedDate = parseValidDate(date) ?: return

        _state.update {
            it.copy(
                selectedDate = parsedDate.toString(),
                selectedTime = null,
                isReservationConfirmed = false,
                errorMessage = null
            )
        }

        refreshSlots()
    }

    private fun isReserved(
        areaId: String,
        date: LocalDate,
        time: String
    ): Boolean {
        return savedReservations.any {
            it.areaId == areaId &&
                    it.date == date.toString() &&
                    it.startTime == time &&
                    it.status !=
                    CommonAreaFirestoreRepository.CANCELLED
        }
    }

    private fun createTimeSlots(
        areaId: String,
        date: LocalDate
    ): List<TimeSlot> {

        return schedules.map { (start, end) ->

            val reserved = isReserved(
                areaId,
                date,
                start
            )

            val pastTime =
                date == LocalDate.now() &&
                        !LocalTime.parse(start)
                            .isAfter(LocalTime.now())

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
        val date = parseValidDate(
            current.selectedDate
        ) ?: return

        val slot = createTimeSlots(areaId, date)
            .firstOrNull {
                it.startTime == time
            }

        if (
            slot?.status != TimeSlotStatus.AVAILABLE
        ) return

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
        val date = parseValidDate(
            current.selectedDate
        ) ?: return

        val time = current.selectedTime ?: return

        val slot = createTimeSlots(areaId, date)
            .firstOrNull {
                it.startTime == time
            }

        if (
            slot?.status != TimeSlotStatus.AVAILABLE
        ) {
            _state.update {
                it.copy(
                    selectedTime = null,
                    errorMessage =
                        "Este horario ya no está disponible."
                )
            }
            return
        }

        val uid = auth.currentUser?.uid ?: return

        _state.update {
            it.copy(
                isSavingReservation = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            try {
                repository.reserve(
                    areaId = areaId,
                    date = date.toString(),
                    startTime = time,
                    endTime = slot.endTime
                )

                if (auth.currentUser?.uid != uid) {
                    _state.update {
                        it.copy(
                            isSavingReservation = false,
                            errorMessage =
                                "La sesión cambió. Vuelve a ingresar."
                        )
                    }
                    return@launch
                }

                savedReservations =
                    savedReservations +
                            CloudCommonAreaReservation(
                                userId = uid,
                                areaId = areaId,
                                date = date.toString(),
                                startTime = time,
                                endTime = slot.endTime,
                                status =
                                    CommonAreaFirestoreRepository.ACTIVE
                            )

                _state.update {
                    it.copy(
                        isSavingReservation = false,
                        isReservationConfirmed = true,
                        selectedAreaId = areaId,
                        selectedDate = date.toString(),
                        selectedTime = time,
                        timeSlots = createTimeSlots(
                            areaId,
                            date
                        ),
                        errorMessage = null
                    )
                }

            } catch (exception: Exception) {
                Log.e(
                    "HabitatAreas",
                    "Error al confirmar reserva",
                    exception
                )

                _state.update {
                    it.copy(
                        isSavingReservation = false,
                        isReservationConfirmed = false,
                        errorMessage =
                            "No se pudo confirmar la reserva en Firestore. Verifica los permisos o si el horario ya fue ocupado."
                    )
                }
            }
        }
    }
}
