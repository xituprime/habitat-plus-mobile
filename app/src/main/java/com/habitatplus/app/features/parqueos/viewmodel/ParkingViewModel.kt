
package com.habitatplus.app.features.parqueos.viewmodel

import android.app.Application
import android.graphics.Bitmap
import com.habitatplus.app.data.repository.ReservationImageRepository
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.habitatplus.app.core.session.UserSessionRepository
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

class ParkingViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = ParkingFirestoreRepository()
    private val imageRepository = ReservationImageRepository()
    private var pendingVehiclePhoto: Bitmap? = null
    fun setVehiclePhoto(bitmap: Bitmap?) { pendingVehiclePhoto = bitmap }
    private val userRepository = UserSessionRepository()

    private val notificationRepository =
        NotificationRepository(application)

    private val auth = FirebaseAuth.getInstance()

    private val _state = MutableStateFlow(ParkingState())
    val state: StateFlow<ParkingState> = _state.asStateFlow()

    private var reservations =
        emptyList<CloudParkingReservation>()

    private var sessionVersion = 0

    init {
        loadParkingCatalog()
        refreshReservations()
    }

    fun onIntent(intent: ParkingIntent) {
        when (intent) {
            ParkingIntent.LoadData -> refreshReservations()

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
            residentParkings = emptyList(),
            visitorParkings = listOf(
                "V-01", "V-02", "V-03", "V-04", "V-05"
            ).map { id ->
                ParkingSpace(
                    id = id,
                    type = ParkingType.VISITOR,
                    status = if (id == "V-03") {
                        ParkingStatus.MAINTENANCE
                    } else {
                        ParkingStatus.AVAILABLE
                    }
                )
            },
            registeredResidentParkings = 120,
            userRole = UserRole.RESIDENT
        )
    }

    fun clearSession() {
        sessionVersion++
        reservations = emptyList()
        _state.value = ParkingState()
        loadParkingCatalog()
    }

    private fun startReservation(parkingId: String) {
        pendingVehiclePhoto = null
        val requestedUid = auth.currentUser?.uid
        val version = sessionVersion

        if (requestedUid == null) {
            _state.value = _state.value.copy(
                error = "Debes iniciar sesión.",
                isLoading = false
            )
            return
        }

        _state.value = _state.value.copy(
            reservationForm = ReservationForm(
                parkingId = parkingId
            ),
            reservationSuccess = false,
            error = null,
            isLoading = true
        )

        viewModelScope.launch {
            try {
                val user = userRepository.getCurrentUser()

                if (!sameSession(requestedUid, version)) {
                    return@launch
                }

                if (
                    user.residenceStatus != "approved" ||
                    user.apartment.isBlank()
                ) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "Tu residencia debe estar aprobada " +
                            "para realizar reservas."
                    )
                    return@launch
                }

                _state.value = _state.value.copy(
                    reservationForm = ReservationForm(
                        parkingId = parkingId,
                        apartment = user.apartment
                    ),
                    reservationSuccess = false,
                    isLoading = false,
                    error = null
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (sameSession(requestedUid, version)) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "No se pudo cargar tu vivienda."
                    )
                }
            }
        }
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
        val current = _state.value

        if (
            current.isSavingReservation ||
            current.reservationSuccess
        ) return

        val form = current.reservationForm

        if (current.isLoading || form.apartment.isBlank()) {
            _state.value = current.copy(
                error = "Espera a que se verifique tu residencia."
            )
            return
        }

        val visitorError = if (form.visitorName.isBlank()) {
            "Ingresa el nombre del visitante."
        } else null

        val plateError = if (form.licensePlate.isBlank()) {
            "Ingresa la placa."
        } else null

        val dateError = if (form.date.isBlank()) {
            "Selecciona una fecha."
        } else null

        val entryError = if (form.entryTime.isBlank()) {
            "Selecciona la hora de ingreso."
        } else null

        val exitError = if (form.exitTime.isBlank()) {
            "Selecciona la hora de salida."
        } else null

        if (
            listOf(
                visitorError,
                plateError,
                dateError,
                entryError,
                exitError
            ).any { it != null }
        ) {
            _state.value = current.copy(
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

        val parking = current.visitorParkings
            .firstOrNull { it.id == form.parkingId }

        if (
            parking == null ||
            parking.status == ParkingStatus.MAINTENANCE
        ) {
            _state.value = current.copy(
                error = "Parqueo no disponible."
            )
            return
        }

        val requestedUid = auth.currentUser?.uid

        if (requestedUid == null) {
            _state.value = current.copy(
                error = "Debes iniciar sesión."
            )
            return
        }

        val version = sessionVersion

        _state.value = current.copy(
            isSavingReservation = true,
            error = null
        )

        viewModelScope.launch {
            try {
                if (!sameSession(requestedUid, version)) {
                    return@launch
                }

                val savedId = repository.createReservation(
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

                if (!sameSession(requestedUid, version)) {
                    return@launch
                }

                val photo = pendingVehiclePhoto
                if (photo != null) {
                    try {
                        imageRepository.save("parking", savedId, ReservationImageRepository.encode(photo))
                        pendingVehiclePhoto = null
                    } catch (e: Exception) {
                        Log.w("ParkingViewModel", "La reserva se guardó, pero la fotografía no.", e)
                    }
                }

                _state.value = _state.value.copy(
                    isSavingReservation = false,
                    reservationSuccess = true,
                    error = null
                )

                refreshReservations()

                try {
                    if (sameSession(requestedUid, version)) {
                        notificationRepository.createNotification(
                            userId = requestedUid,
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
                    Log.w(
                        "ParkingViewModel",
                        "No se pudo guardar la notificación.",
                        e
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (sameSession(requestedUid, version)) {
                    _state.value = _state.value.copy(
                        isSavingReservation = false,
                        reservationSuccess = false,
                        error = e.message
                            ?: "No se pudo guardar la reserva."
                    )
                }
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

        val requestedUid = auth.currentUser?.uid ?: return
        val version = sessionVersion

        viewModelScope.launch {
            try {
                repository.cancelReservation(reservationId)

                if (!sameSession(requestedUid, version)) {
                    return@launch
                }

                _state.value = _state.value.copy(error = null)
                refreshReservations()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (sameSession(requestedUid, version)) {
                    _state.value = _state.value.copy(
                        error = e.message
                            ?: "No se pudo cancelar la reserva."
                    )
                }
            }
        }
    }

    private fun refreshReservations() {
        val requestedUid = auth.currentUser?.uid ?: run {
            clearSession()
            return
        }

        val version = sessionVersion

        viewModelScope.launch {
            try {
                val loaded = repository.getMyReservations()

                if (!sameSession(requestedUid, version)) {
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
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (sameSession(requestedUid, version)) {
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
        val search = current.historySearch.trim()

        val filtered = current.recentHistory.filter { reservation ->
            val matchesSearch =
                search.isBlank() ||
                reservation.visitorName.contains(search, true) ||
                reservation.parkingId.contains(search, true) ||
                reservation.date.contains(search, true)

            val matchesFilter = when (current.historyFilter) {
                "Activas" -> reservation.status == "Activa"
                "Canceladas" -> reservation.status == "Cancelada"
                "Finalizadas" -> reservation.status == "Finalizada"
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

    private fun sameSession(
        uid: String,
        version: Int
    ): Boolean {
        return auth.currentUser?.uid == uid &&
            sessionVersion == version
    }
}
