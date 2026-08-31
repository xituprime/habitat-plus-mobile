package com.habitatplus.app.features.parqueos.viewmodel

import androidx.lifecycle.ViewModel
import com.habitatplus.app.features.parqueos.model.ParkingHistory
import com.habitatplus.app.features.parqueos.model.ParkingSpace
import com.habitatplus.app.features.parqueos.model.ParkingStatus
import com.habitatplus.app.features.parqueos.model.ParkingType
import com.habitatplus.app.features.parqueos.model.UserRole
import com.habitatplus.app.features.parqueos.state.ParkingState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ParkingViewModel: ViewModel() {

    private val _state = MutableStateFlow(ParkingState())

    val state: StateFlow<ParkingState> =  _state.asStateFlow()

    init{
        loadMockData()
    }

    private fun loadMockData(){

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
                    visitorName = "Carlos Perez",
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
