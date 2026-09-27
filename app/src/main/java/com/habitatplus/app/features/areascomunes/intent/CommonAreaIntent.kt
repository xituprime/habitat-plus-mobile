package com.habitatplus.app.features.areascomunes.intent

sealed interface CommonAreaIntent {

    data object LoadAreas : CommonAreaIntent

    data object LoadAvailability : CommonAreaIntent

    data class SelectArea(
        val areaId: String
    ) : CommonAreaIntent

    data class SelectDate(
        val date: String
    ) : CommonAreaIntent

    data class SelectTime(
        val time: String
    ) : CommonAreaIntent

    data object ConfirmReservation : CommonAreaIntent
}