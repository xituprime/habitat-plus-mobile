package com.habitatplus.app.features.dashboard.intent

sealed interface DashboardIntent {
    data object LoadDashboard : DashboardIntent
    data object RefreshDashboard : DashboardIntent
}