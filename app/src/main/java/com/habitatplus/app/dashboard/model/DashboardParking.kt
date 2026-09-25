package com.habitatplus.app.features.dashboard.model

data class DashboardParking(
    val assignedSpace: String,
    val level: String,
    val isActive: Boolean,
    val availableVisitorSpaces: Int,
    val totalVisitorSpaces: Int
)