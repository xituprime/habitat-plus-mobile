package com.habitatplus.app.features.parqueos.viewmodel

import androidx.lifecycle.ViewModel
import com.habitatplus.app.features.parqueos.intent.ParkingIntent
import com.habitatplus.app.features.parqueos.model.ParkingHistory
import com.habitatplus.app.features.parqueos.model.ParkingSpace
import com.habitatplus.app.features.parqueos.model.ParkingStatus
import com.habitatplus.app.features.parqueos.model.ParkingType
import com.habitatplus.app.features.parqueos.model.ReservationForm
import com.habitatplus.app.features.parqueos.model.UserRole
import com.habitatplus.app.features.parqueos.state.ParkingState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.habitatplus.app.data.local.DatabaseProvider
import com.habitatplus.app.data.local.ParkingReservationEntity
import com.habitatplus.app.data.repository.ParkingRepository
import kotlinx.coroutines.launch

class ParkingViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = ParkingRepository(
        DatabaseProvider
            .getDatabase(application)
            .parkingReservationDao()
    )

    private val _state = MutableStateFlow(ParkingState())

    val state: StateFlow<ParkingState> = _state.asStateFlow()

    init {
        loadMockData()
        observeSavedReservations()
    }

    fun onIntent(intent: ParkingIntent) {
        when (intent) {

            ParkingIntent.LoadData -> {
                if (_state.value.residentParkings.isEmpty() &&
                    _state.value.visitorParkings.isEmpty()
                ) {
                    loadMockData()
                }
            }

            is ParkingIntent.StartReservation -> {
                startReservation(intent.parkingId)
            }

            is ParkingIntent.VisitorNameChanged -> {
                updateReservationForm(
                    visitorName = intent.value
                )
            }

            is ParkingIntent.LicensePlateChanged -> {
                updateReservationForm(
                    licensePlate = intent.value.uppercase()
                )
            }

            is ParkingIntent.VehicleBrandChanged -> {
                updateReservationForm(
                    vehicleBrand = intent.value
                )
            }

            is ParkingIntent.VehicleColorChanged -> {
                updateReservationForm(
                    vehicleColor = intent.value
                )
            }

            is ParkingIntent.DateChanged -> {
                updateReservationForm(
                    date = intent.value
                )
            }

            is ParkingIntent.EntryTimeChanged -> {
                updateReservationForm(
                    entryTime = intent.value
                )
            }

            is ParkingIntent.ExitTimeChanged -> {
                updateReservationForm(
                    exitTime = intent.value
                )
            }

            ParkingIntent.SubmitReservation -> {
                submitReservation()
            }

            is ParkingIntent.ReserveParking -> Unit
            is ParkingIntent.CancelReservation -> Unit
            ParkingIntent.OpenHistory -> Unit
            ParkingIntent.AddParking -> Unit
            is ParkingIntent.EditParking -> Unit
            is ParkingIntent.DeleteParking -> Unit
            is ParkingIntent.ChangeParkingStatus -> Unit

            is ParkingIntent.HistorySearchChanged -> {
                _state.value = _state.value.copy(
                    historySearch = intent.value
                )
                updateFilteredHistory()
            }

            is ParkingIntent.HistoryFilterChanged -> {
                _state.value = _state.value.copy(
                    historyFilter = intent.value
                )
                updateFilteredHistory()
            }
        }
    }

    private fun startReservation(parkingId: String) {
        _state.value = _state.value.copy(
            reservationForm = ReservationForm(
                parkingId = parkingId,
                apartment = "A-203"
            ),
            reservationSuccess = false,
            error = null
        )
    }

    private fun updateReservationForm(
        visitorName: String? = null,
        licensePlate: String? = null,
        vehicleBrand: String? = null,
        vehicleColor: String? = null,
        date: String? = null,
        entryTime: String? = null,
        exitTime: String? = null
    ) {
        val current = _state.value.reservationForm

        _state.value = _state.value.copy(
            reservationForm = current.copy(
                visitorName = visitorName ?: current.visitorName,
                licensePlate = licensePlate ?: current.licensePlate,
                vehicleBrand = vehicleBrand ?: current.vehicleBrand,
                vehicleColor = vehicleColor ?: current.vehicleColor,
                date = date ?: current.date,
                entryTime = entryTime ?: current.entryTime,
                exitTime = exitTime ?: current.exitTime,

                visitorNameError =
                    if (visitorName != null) null else current.visitorNameError,

                licensePlateError =
                    if (licensePlate != null) null else current.licensePlateError,

                dateError =
                    if (date != null) null else current.dateError,

                entryTimeError =
                    if (entryTime != null) null else current.entryTimeError,

                exitTimeError =
                    if (exitTime != null) null else current.exitTimeError
            ),
            reservationSuccess = false
        )
    }

    private fun submitReservation() {
        if (_state.value.reservationSuccess) return
        val form = _state.value.reservationForm

        val visitorNameError =
            if (form.visitorName.isBlank()) {
                "El nombre del visitante es obligatorio"
            } else {
                null
            }

        val licensePlateError =
            if (form.licensePlate.isBlank()) {
                "La placa es obligatoria"
            } else {
                null
            }

        val dateError =
            if (form.date.isBlank()) {
                "La fecha es obligatoria"
            } else {
                null
            }

        val entryTimeError =
            if (form.entryTime.isBlank()) {
                "La hora de ingreso es obligatoria"
            } else {
                null
            }

        var exitTimeError =
            if (form.exitTime.isBlank()) {
                "La hora de salida es obligatoria"
            } else {
                null
            }

        if (
            entryTimeError == null &&
            exitTimeError == null &&
            form.exitTime <= form.entryTime
        ) {
            exitTimeError =
                "La hora de salida debe ser mayor a la hora de ingreso"
        }

        val hasErrors =
            visitorNameError != null ||
                    licensePlateError != null ||
                    dateError != null ||
                    entryTimeError != null ||
                    exitTimeError != null

        if (hasErrors) {
            _state.value = _state.value.copy(
                reservationForm = form.copy(
                    visitorNameError = visitorNameError,
                    licensePlateError = licensePlateError,
                    dateError = dateError,
                    entryTimeError = entryTimeError,
                    exitTimeError = exitTimeError
                ),
                reservationSuccess = false
            )
            updateFilteredHistory()
            updateHistoryStatistics()

            return
        }

        val updatedParkings =
            _state.value.visitorParkings.map { parking ->

                if (parking.id == form.parkingId) {
                    parking.copy(
                        status = ParkingStatus.OCCUPIED,
                        occupiedUntil = form.exitTime
                    )
                } else {
                    parking
                }
            }

        val newHistory = ParkingHistory(
            visitorName = form.visitorName,
            parkingId = form.parkingId,
            date = form.date,
            time = form.entryTime,
            status = "Activa"
        )

        viewModelScope.launch {
            try {
                // 1. Guardar primero en Room
                repository.saveReservation(
                    ParkingReservationEntity(
                        parkingId = form.parkingId,
                        visitorName = form.visitorName,
                        licensePlate = form.licensePlate,
                        vehicleBrand = form.vehicleBrand,
                        vehicleColor = form.vehicleColor,
                        apartment = form.apartment,
                        date = form.date,
                        entryTime = form.entryTime,
                        exitTime = form.exitTime,
                        status = "Activa"
                    )
                )

                // 2. Actualizar el estado MVI
                _state.value = _state.value.copy(
                    visitorParkings = updatedParkings,
                    recentHistory = listOf(newHistory) +
                            _state.value.recentHistory,
                    reservationSuccess = true,
                    error = null
                )

                // 3. Actualizar historial y estadísticas
                updateFilteredHistory()
                updateHistoryStatistics()

            } catch (exception: Exception) {
                _state.value = _state.value.copy(
                    reservationSuccess = false,
                    error = "No se pudo guardar la reserva localmente."
                )
            }
        }

        _state.value = _state.value.copy(
            reservationSuccess = true,
            error = null
        )

        if (_state.value.isSavingReservation ||
            _state.value.reservationSuccess
        ) return

        _state.value = _state.value.copy(
            isSavingReservation = true,
            error = null
        )
        viewModelScope.launch {
            try {
                repository.saveReservation(
                    ParkingReservationEntity(
                        parkingId = form.parkingId,
                        visitorName = form.visitorName,
                        licensePlate = form.licensePlate,
                        vehicleBrand = form.vehicleBrand,
                        vehicleColor = form.vehicleColor,
                        apartment = form.apartment,
                        date = form.date,
                        entryTime = form.entryTime,
                        exitTime = form.exitTime
                    )
                )
                _state.value = _state.value.copy(
                    isSavingReservation = false,
                    reservationSuccess = true,
                    error = null
                )
            } catch (exception: Exception) {
                _state.value = _state.value.copy(
                    isSavingReservation = false,
                    reservationSuccess = false,
                    error = "No se pudo guardar la reserva localmente."
                )
            }
        }
    }

    private fun loadMockData() {
        _state.value = ParkingState(
            residentParkings = listOf(
                ParkingSpace(
                    id = "P-12",
                    apartment = "A-203",
                    type = ParkingType.RESIDENT,
                    status = ParkingStatus.ASSIGNED
                )
            ),

            visitorParkings = listOf(
                ParkingSpace(
                    id = "V-01",
                    type = ParkingType.VISITOR,
                    status = ParkingStatus.AVAILABLE
                ),

                ParkingSpace(
                    id = "V-02",
                    type = ParkingType.VISITOR,
                    status = ParkingStatus.OCCUPIED,
                    occupiedUntil = "18:00"
                ),

                ParkingSpace(
                    id = "V-03",
                    type = ParkingType.VISITOR,
                    status = ParkingStatus.MAINTENANCE
                ),

                ParkingSpace(
                    id = "V-04",
                    type = ParkingType.VISITOR,
                    status = ParkingStatus.AVAILABLE
                ),
                ParkingSpace(
                    id = "V-05",
                    type = ParkingType.VISITOR,
                    status = ParkingStatus.AVAILABLE
                )

            ),

            recentHistory = listOf(
                ParkingHistory(
                    visitorName = "Carlos Pérez",
                    parkingId = "V-02",
                    date = "Hoy",
                    time = "17:00",
                    status = "Finalizada"
                )

            ),

            registeredResidentParkings = 120,
            userRole = UserRole.RESIDENT
        )
        updateFilteredHistory()
        updateHistoryStatistics()
    }

    private fun updateFilteredHistory() {
        val current = _state.value

        val filtered = current.recentHistory.filter { reservation ->
            val matchesSearch =
                current.historySearch.isBlank() ||
                        reservation.visitorName.contains(
                            current.historySearch,
                            ignoreCase = true
                        ) ||
                        reservation.parkingId.contains(
                            current.historySearch,
                            ignoreCase = true
                        ) ||
                        reservation.date.contains(
                            current.historySearch,
                            ignoreCase = true
                        )

            val matchesFilter = when (current.historyFilter) {
                "Activas" -> reservation.status == "Activa"
                "Finalizadas" -> reservation.status == "Finalizada"
                "Canceladas" -> reservation.status == "Cancelada"
                else -> true
            }

            matchesSearch && matchesFilter
        }

        _state.value = current.copy(
            filteredHistory = filtered
        )
    }

    private fun updateHistoryStatistics() {
        val history = _state.value.recentHistory

        val mostUsed = history
            .groupingBy { it.parkingId }
            .eachCount()
            .maxByOrNull { it.value }
            ?.key ?: "-"

        _state.value = _state.value.copy(
            totalReservations = history.size,
            totalVisitors = history
                .map { it.visitorName.trim().lowercase() }
                .distinct()
                .size,
            mostUsedParking = mostUsed
        )
    }


    private fun observeSavedReservations() {
        viewModelScope.launch {
            repository.reservations.collect { savedReservations ->

                val savedHistory = savedReservations.map { reservation ->
                    ParkingHistory(
                        visitorName = reservation.visitorName,
                        parkingId = reservation.parkingId,
                        date = reservation.date,
                        time = reservation.entryTime,
                        status = reservation.status
                    )
                }

                val occupiedIds = savedReservations
                    .filter { it.status == "Activa" }
                    .map { it.parkingId }
                    .toSet()

                val updatedParkings =
                    _state.value.visitorParkings.map { parking ->
                        if (parking.id in occupiedIds) {
                            val reservation = savedReservations
                                .firstOrNull {
                                    it.parkingId == parking.id &&
                                            it.status == "Activa"
                                }

                            parking.copy(
                                status = ParkingStatus.OCCUPIED,
                                occupiedUntil = reservation?.exitTime
                            )
                        } else {
                            parking
                        }
                    }

                _state.value = _state.value.copy(
                    visitorParkings = updatedParkings,
                    recentHistory = savedHistory
                )

                updateFilteredHistory()
                updateHistoryStatistics()
            }
        }
    }

}