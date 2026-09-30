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

    private val _state = MutableStateFlow(
        CommonAreaState()
    )

    val state: StateFlow<CommonAreaState> =
        _state.asStateFlow()

    fun onIntent(intent: CommonAreaIntent) {

        when (intent) {

            CommonAreaIntent.LoadAreas -> {
                loadAreas()
            }

            CommonAreaIntent.LoadAvailability -> {
                loadAvailability()
            }

            is CommonAreaIntent.SelectArea -> {

                _state.update {
                    it.copy(
                        selectedAreaId = intent.areaId
                    )
                }
            }

            is CommonAreaIntent.SelectDate -> {

                _state.update {
                    it.copy(
                        selectedDate = intent.date,
                        selectedTime = null
                    )
                }
            }

            is CommonAreaIntent.SelectTime -> {

                val selectedSlot =
                    _state.value.timeSlots.find {
                        it.startTime == intent.time
                    }

                if (
                    selectedSlot?.status ==
                    TimeSlotStatus.AVAILABLE
                ) {

                    _state.update {
                        it.copy(
                            selectedTime = intent.time
                        )
                    }
                }
            }

            CommonAreaIntent.ConfirmReservation -> {

                val currentState = _state.value

                if (
                    currentState.selectedAreaId != null &&
                    currentState.selectedDate != null &&
                    currentState.selectedTime != null
                ) {

                    _state.update {
                        it.copy(
                            isReservationConfirmed = true
                        )
                    }
                }
            }
        }
    }

    private fun loadAreas() {

        val areas = listOf(

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

        _state.update {
            it.copy(
                isLoading = false,
                areas = areas
            )
        }
    }

    private fun loadAvailability() {

        val slots = listOf(

            TimeSlot(
                startTime = "09:00",
                endTime = "10:00",
                description = "Turno matutino",
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

        _state.update {
            it.copy(
                isLoading = false,
                selectedAreaId = "court",
                selectedDate = "15 de Septiembre",
                timeSlots = slots
            )
        }
    }
}