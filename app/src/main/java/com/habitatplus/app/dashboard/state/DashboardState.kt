package com.habitatplus.app.features.dashboard.state

import com.habitatplus.app.features.dashboard.model.DashboardAnnouncement
import com.habitatplus.app.features.dashboard.model.DashboardParking
import com.habitatplus.app.features.dashboard.model.DashboardReservation
import com.habitatplus.app.features.dashboard.model.DashboardReport
import com.habitatplus.app.features.dashboard.model.DashboardMaintenance
data class DashboardState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val condominiumName: String = "",
    val apartmentNumber: String = "",
    val errorMessage: String? = null,
    val announcement: DashboardAnnouncement? = null,
    val parking: DashboardParking? = null,
    val reservation: DashboardReservation? = null,
    val reports: List<DashboardReport> = emptyList(),
    val maintenance: DashboardMaintenance? = null
)
