package com.habitatplus.app.features.parqueos.model

data class ReservationForm(
    val parkingId: String = "",
    val visitorName: String = "",
    val licensePlate: String = "",
    val vehicleBrand: String = "",
    val vehicleColor: String = "",
    val apartment: String = "A-203",
    val date: String = "",
    val entryTime: String = "",
    val exitTime: String = "",
    val photoUri: String? = null,

    val visitorNameError: String? = null,
    val licensePlateError: String? = null,
    val dateError: String? = null,
    val entryTimeError: String? = null,
    val exitTimeError: String? = null
)