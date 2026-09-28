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

class ParkingViewModel : ViewModel() {

    private val _state = MutableStateFlow(ParkingState())

    val state: StateFlow<ParkingState> = _state.asStateFlow()

    init {
        loadMockData()
    }

    fun onIntent(intent: ParkingIntent) {
        when (intent) {

            ParkingIntent.LoadData -> {
                loadMockData()
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

        _state.value = _state.value.copy(
            visitorParkings = updatedParkings,
            recentHistory = listOf(newHistory) + _state.value.recentHistory,
            reservationSuccess = true,
            error = null
        )
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
    }
}