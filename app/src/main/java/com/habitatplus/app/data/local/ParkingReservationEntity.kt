package com.habitatplus.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "parking_reservations")
data class ParkingReservationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val parkingId: String,
    val visitorName: String,
    val licensePlate: String,
    val vehicleBrand: String,
    val vehicleColor: String,
    val apartment: String,
    val date: String,
    val entryTime: String,
    val exitTime: String,
    val status: String = "Activa"
)
