package com.habitatplus.app.features.dashboard.intent

sealed interface DashboardIntent {
    data object Load : DashboardIntent
    data object Refresh : DashboardIntent
}