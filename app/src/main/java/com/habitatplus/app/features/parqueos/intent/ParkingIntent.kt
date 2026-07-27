package com.habitatplus.app.features.parqueos.intent

sealed interface ParkingIntent{

    data object LoadData : ParkingIntent

    data class ReserveParking(
        val parkingId: String
    ) : ParkingIntent

    data class CancelReservation(
        val parkingId: String
    ) : ParkingIntent

    data object OpenHistory : ParkingIntent

    data object AddParking : ParkingIntent

    data class EditParking(
        val parkingId: String
    ) : ParkingIntent

    data class DeleteParking(
        val parkingId: String
    ) : ParkingIntent

    data class ChangeParkingStatus(
        val parkingId: String
    ) : ParkingIntent
}