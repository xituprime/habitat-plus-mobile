package com.habitatplus.app.features.parqueos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.habitatplus.app.data.repository.CloudParkingReservation
import com.habitatplus.app.data.repository.ParkingFirestoreRepository
import com.habitatplus.app.features.notificaciones.data.NotificationRepository
import com.habitatplus.app.features.notificaciones.model.NotificationType
import com.habitatplus.app.features.parqueos.intent.ParkingIntent
import com.habitatplus.app.features.parqueos.model.*
import com.habitatplus.app.features.parqueos.state.ParkingState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth

class ParkingViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ParkingFirestoreRepository()
    private val notificationRepository = NotificationRepository(application)
    private val auth = FirebaseAuth.getInstance()

    private val _state = MutableStateFlow(ParkingState())
    val state: StateFlow<ParkingState> = _state.asStateFlow()

    private var reservations =
        emptyList<CloudParkingReservation>()

    init {
        loadParkingCatalog()
        refreshReservations()
    }

    fun onIntent(intent: ParkingIntent) {
        when (intent) {
            ParkingIntent.LoadData ->
                refreshReservations()

            is ParkingIntent.StartReservation ->
                startReservation(intent.parkingId)

            is ParkingIntent.VisitorNameChanged ->
                updateForm(visitorName = intent.value)

            is ParkingIntent.LicensePlateChanged ->
                updateForm(licensePlate = intent.value.uppercase())

            is ParkingIntent.VehicleBrandChanged ->
                updateForm(vehicleBrand = intent.value)

            is ParkingIntent.VehicleColorChanged ->
                updateForm(vehicleColor = intent.value)

            is ParkingIntent.DateChanged ->
                updateForm(date = intent.value)

            is ParkingIntent.EntryTimeChanged ->
                updateForm(entryTime = intent.value)

            is ParkingIntent.ExitTimeChanged ->
                updateForm(exitTime = intent.value)

            ParkingIntent.SubmitReservation ->
                submitReservation()

            is ParkingIntent.CancelReservation ->
                cancelReservation(intent.parkingId)

            ParkingIntent.OpenHistory ->
                refreshReservations()

            is ParkingIntent.HistorySearchChanged -> {
                _state.value = _state.value.copy(
                    historySearch = intent.value
                )
                filterHistory()
            }

            is ParkingIntent.HistoryFilterChanged -> {
                _state.value = _state.value.copy(
                    historyFilter = intent.value
                )
                filterHistory()
            }

            is ParkingIntent.ReserveParking -> Unit
            ParkingIntent.AddParking -> Unit
            is ParkingIntent.EditParking -> Unit
            is ParkingIntent.DeleteParking -> Unit
            is ParkingIntent.ChangeParkingStatus -> Unit
        }
    }

    private fun loadParkingCatalog() {
        _state.value = _state.value.copy(
            residentParkings = listOf(
                ParkingSpace(
                    id = "P-12",
                    apartment = "A-203",
                    type = ParkingType.RESIDENT,
                    status = ParkingStatus.ASSIGNED
                )
            ),
            visitorParkings = listOf(
                "V-01", "V-02", "V-03", "V-04", "V-05"
            ).map { id ->
                ParkingSpace(
                    id = id,
                    type = ParkingType.VISITOR,
                    status = if (id == "V-03")
                        ParkingStatus.MAINTENANCE
                    else ParkingStatus.AVAILABLE
                )
            },
            registeredResidentParkings = 120,
            userRole = UserRole.RESIDENT
        )

    }

    fun clearSession() {
        reservations = emptyList()

        _state.value = ParkingState()

        loadParkingCatalog()
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

    private fun updateForm(
        visitorName: String? = null,
        licensePlate: String? = null,
        vehicleBrand: String? = null,
        vehicleColor: String? = null,
        date: String? = null,
        entryTime: String? = null,
        exitTime: String? = null
    ) {
        val form = _state.value.reservationForm

        _state.value = _state.value.copy(
            reservationForm = form.copy(
                visitorName = visitorName ?: form.visitorName,
                licensePlate = licensePlate ?: form.licensePlate,
                vehicleBrand = vehicleBrand ?: form.vehicleBrand,
                vehicleColor = vehicleColor ?: form.vehicleColor,
                date = date ?: form.date,
                entryTime = entryTime ?: form.entryTime,
                exitTime = exitTime ?: form.exitTime,
                visitorNameError = null,
                licensePlateError = null,
                dateError = null,
                entryTimeError = null,
                exitTimeError = null
            ),
            reservationSuccess = false,
            error = null
        )
    }

    private fun submitReservation() {
        val state = _state.value

        if (state.isSavingReservation ||
            state.reservationSuccess
        ) return

        val form = state.reservationForm

        val visitorError =
            if (form.visitorName.isBlank())
                "Ingresa el nombre del visitante."
            else null

        val plateError =
            if (form.licensePlate.isBlank())
                "Ingresa la placa."
            else null

        val dateError =
            if (form.date.isBlank())
                "Ingresa una fecha."
            else null

        val entryError =
            if (form.entryTime.isBlank())
                "Ingresa la hora de ingreso."
            else null

        val exitError =
            if (form.exitTime.isBlank())
                "Ingresa la hora de salida."
            else null

        if (listOf(
                visitorError, plateError, dateError,
                entryError, exitError
            ).any { it != null }
        ) {
            _state.value = state.copy(
                reservationForm = form.copy(
                    visitorNameError = visitorError,
                    licensePlateError = plateError,
                    dateError = dateError,
                    entryTimeError = entryError,
                    exitTimeError = exitError
                )
            )
            return
        }

        val parking = state.visitorParkings
            .firstOrNull { it.id == form.parkingId }

        if (parking == null ||
            parking.status == ParkingStatus.MAINTENANCE
        ) {
            _state.value = state.copy(
                error = "Parqueo no disponible."
            )
            return
        }

        _state.value = state.copy(
            isSavingReservation = true,
            error = null
        )

        val reservationUserId = auth.currentUser?.uid
        if (reservationUserId.isNullOrBlank()) {
            _state.value = _state.value.copy(
                isSavingReservation = false,
                error = "Debes iniciar sesión para reservar."
            )
            return
        }

        viewModelScope.launch {
            try {
                if (auth.currentUser?.uid != reservationUserId) {
                    throw IllegalStateException("La sesión cambió.")
                }

                repository.createReservation(
                    CloudParkingReservation(
                        parkingId = form.parkingId,
                        visitorName = form.visitorName.trim(),
                        licensePlate = form.licensePlate.trim(),
                        vehicleBrand = form.vehicleBrand.trim(),
                        vehicleColor = form.vehicleColor.trim(),
                        apartment = form.apartment,
                        date = form.date.trim(),
                        entryTime = form.entryTime.trim(),
                        exitTime = form.exitTime.trim()
                    )
                )

                _state.value = _state.value.copy(
                    isSavingReservation = false,
                    reservationSuccess = true,
                    error = null
                )

                refreshReservations()

                try {
                    if (auth.currentUser?.uid == reservationUserId) {
                        notificationRepository.createNotification(
                            userId = reservationUserId,
                            type = NotificationType.PARKING,
                            title = "Reserva de parqueo confirmada",
                            message = "Parqueo ${form.parkingId}, " +
                                    "fecha ${form.date}, " +
                                    "de ${form.entryTime} a ${form.exitTime}.",
                            relatedId = null
                        )
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    android.util.Log.w(
                        "ParkingViewModel",
                        "No se pudo guardar la notificación de parqueo.",
                        e
                    )
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isSavingReservation = false,
                    reservationSuccess = false,
                    error = e.message
                        ?: "No se pudo guardar la reserva."
                )
            }
        }
    }

    private fun cancelReservation(reservationId: String) {
        if (reservationId.isBlank()) {
            _state.value = _state.value.copy(
                error = "Identificador de reserva inválido."
            )
            return
        }

        viewModelScope.launch {
            try {
                repository.cancelReservation(reservationId)
                _state.value = _state.value.copy(error = null)
                refreshReservations()
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message
                        ?: "No se pudo cancelar la reserva."
                )
            }
        }
    }


    private fun refreshReservations() {
        val requestedUid = FirebaseAuth.getInstance()
            .currentUser?.uid ?: run {
            clearSession()
            return
        }

        viewModelScope.launch {
            try {
                val loaded = repository.getMyReservations()

                val currentUid = FirebaseAuth.getInstance()
                    .currentUser?.uid

                if (currentUid != requestedUid) {
                    return@launch
                }

                reservations = loaded

                val history = loaded.map {
                    ParkingHistory(
                        visitorName = it.visitorName,
                        parkingId = it.parkingId,
                        date = it.date,
                        time = it.entryTime,
                        status = it.status,
                        reservationId = it.id
                    )
                }

                _state.value = _state.value.copy(
                    recentHistory = history,
                    error = null
                )

                filterHistory()
                updateStatistics()

            } catch (e: Exception) {
                if (FirebaseAuth.getInstance()
                        .currentUser?.uid == requestedUid
                ) {
                    _state.value = _state.value.copy(
                        error = e.message
                            ?: "No se pudo cargar el historial."
                    )
                }
            }
        }
    }


    private fun filterHistory() {
        val current = _state.value

        val filtered = current.recentHistory.filter {
            val search = current.historySearch

            val matchesSearch =
                search.isBlank() ||
                        it.visitorName.contains(search, true) ||
                        it.parkingId.contains(search, true) ||
                        it.date.contains(search, true)

            val matchesFilter = when (
                current.historyFilter
            ) {
                "Activas" -> it.status == "Activa"
                "Canceladas" -> it.status == "Cancelada"
                "Finalizadas" -> it.status == "Finalizada"
                else -> true
            }

            matchesSearch && matchesFilter
        }

        _state.value = current.copy(
            filteredHistory = filtered
        )
    }

    private fun updateStatistics() {
        val history = _state.value.recentHistory

        val mostUsed = history
            .groupingBy { it.parkingId }
            .eachCount()
            .maxByOrNull { it.value }
            ?.key ?: "-"

        _state.value = _state.value.copy(
            totalReservations = history.size,
            totalVisitors = history
                .map { it.visitorName.lowercase().trim() }
                .distinct()
                .size,
            mostUsedParking = mostUsed
        )
    }
}
