package com.habitatplus.app.features.parqueos.state

import com.habitatplus.app.features.parqueos.model.ParkingHistory
import com.habitatplus.app.features.parqueos.model.ParkingSpace
import com.habitatplus.app.features.parqueos.model.UserRole

data class ParkingState(
    val residentParkings: List<ParkingSpace> = emptyList(),

    val visitorParkings: List<ParkingSpace> = emptyList(),

    val recentHistory: List<ParkingHistory> = emptyList(),

    val userRole: UserRole = UserRole.RESIDENT,

    val registeredResidentParkings: Int = 0,

    val isLoading: Boolean = false,

    val error: String? = null

)