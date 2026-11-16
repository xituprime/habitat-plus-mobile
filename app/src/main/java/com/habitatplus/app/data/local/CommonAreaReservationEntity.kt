package com.habitatplus.app.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "common_area_reservations",
    indices = [
        Index(
            value = ["areaId", "date", "startTime"],
            unique = true
        )
    ]
)
data class CommonAreaReservationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val areaId: String,
    val date: String,
    val startTime: String,
    val endTime: String
)
