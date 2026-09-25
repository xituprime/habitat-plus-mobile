package com.habitatplus.app.features.dashboard.model

data class DashboardReservation(
    val areaName: String,
    val schedule: String,
    val status: String,
    val accessCode: String
)