package com.habitatplus.app.features.dashboard.intent

import com.habitatplus.app.features.dashboard.model.DashboardNavigation
import com.habitatplus.app.features.dashboard.model.DashboardTab

sealed interface DashboardIntent {

    data object LoadRemoteData : DashboardIntent

    data object Load : DashboardIntent

    data object Refresh : DashboardIntent

    data object OpenAnnouncements : DashboardIntent

    data class OpenAnnouncement(
        val id: String
    ) : DashboardIntent

    data object OpenParking : DashboardIntent

    data object OpenAreas : DashboardIntent

    data object OpenReports : DashboardIntent

    data object OpenProfile : DashboardIntent

    data class SelectTab(
        val tab: DashboardTab
    ) : DashboardIntent

    data class NavigationHandled(
        val navigation: DashboardNavigation
    ) : DashboardIntent
}