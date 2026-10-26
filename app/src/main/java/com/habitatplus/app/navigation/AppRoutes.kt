
package com.habitatplus.app.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface AppRoute {

    @Serializable
    data object Home : AppRoute

    @Serializable
    data object Parking : AppRoute

    @Serializable
    data object ParkingReservation : AppRoute

    @Serializable
    data object ParkingHistory : AppRoute

    @Serializable
    data object Reports : AppRoute

    @Serializable
    data object CreateReport : AppRoute

    @Serializable
    data object ReportDetail : AppRoute

    @Serializable
    data object Areas : AppRoute

    @Serializable
    data object AreaAvailability : AppRoute

    @Serializable
    data object ConfirmAreaReservation : AppRoute

    @Serializable
    data object Announcements : AppRoute

    @Serializable
    data class AnnouncementDetail(
        val announcementId: String
    ) : AppRoute

    @Serializable
    data object Profile : AppRoute

    @Serializable
    data object Settings : AppRoute
    @Serializable
    data object Splash : AppRoute

    @Serializable
    data object Login : AppRoute

    @Serializable
    data object Register : AppRoute
}
