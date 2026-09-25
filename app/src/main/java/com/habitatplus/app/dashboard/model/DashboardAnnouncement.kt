package com.habitatplus.app.features.dashboard.model

data class DashboardAnnouncement(
    val title: String,
    val schedule: String,
    val description: String,
    val isUrgent: Boolean = false
)