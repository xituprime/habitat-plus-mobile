package com.habitatplus.app.features.dashboard.model

data class DashboardAnnouncement(
    val id: String,
    val title: String,
    val schedule: String,
    val description: String,
    val urgent: Boolean
)

data class DashboardParking(
    val space: String,
    val location: String,
    val active: Boolean
)

data class DashboardReservation(
    val area: String,
    val schedule: String,
    val status: String,
    val accessCode: String
)

data class DashboardReport(
    val id: String,
    val title: String,
    val detail: String,
    val status: String
)

data class DashboardMaintenance(
    val title: String,
    val date: String,
    val description: String
)