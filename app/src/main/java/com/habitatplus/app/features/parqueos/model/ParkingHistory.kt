package com.habitatplus.app.features.parqueos.model

data class ParkingHistory(
    val visitorName: String,
    val parkingId: String,
    val date: String,
    val time: String,
    val status: String
)
