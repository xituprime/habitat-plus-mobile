package com.habitatplus.app.features.dashboard.state

import com.habitatplus.app.features.dashboard.model.DashboardAnnouncement
import com.habitatplus.app.features.dashboard.model.DashboardMaintenance
import com.habitatplus.app.features.dashboard.model.DashboardParking
import com.habitatplus.app.features.dashboard.model.DashboardReport
import com.habitatplus.app.features.dashboard.model.DashboardReservation

data class DashboardState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val isOffline: Boolean = false,
    val greeting: String = "",
    val userName: String = "",
    val residence: String = "",
    val announcement: DashboardAnnouncement? = null,
    val parkings: List<DashboardParking> = emptyList(),
    val freeVisitorSpaces: Int = 0,
    val totalVisitorSpaces: Int = 0,
    val reservation: DashboardReservation? = null,
    val reports: List<DashboardReport> = emptyList(),
    val maintenance: DashboardMaintenance? = null
)