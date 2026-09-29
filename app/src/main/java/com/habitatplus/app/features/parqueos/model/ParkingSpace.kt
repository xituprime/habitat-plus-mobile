package com.habitatplus.app.features.parqueos.model

data class ParkingSpace(
    val id: String,
    val apartment: String? = null,
    val type: ParkingType,
    val status: ParkingStatus,
    val occupiedUntil: String? = null
)