package com.habitatplus.app.features.dashboard.model

enum class DashboardTab {
    HOME,
    PARKING,
    REPORTS,
    AREAS,
    PROFILE
}

sealed interface DashboardNavigation {

    data object Announcements : DashboardNavigation

    data class Announcement(
        val id: String
    ) : DashboardNavigation

    data object Parking : DashboardNavigation

    data object Areas : DashboardNavigation

    data object Reports : DashboardNavigation

    data object Profile : DashboardNavigation

    data class Tab(
        val destination: DashboardTab
    ) : DashboardNavigation
}