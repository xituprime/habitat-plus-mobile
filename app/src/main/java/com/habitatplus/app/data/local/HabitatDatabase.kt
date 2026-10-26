package com.habitatplus.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        ParkingReservationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class HabitatDatabase : RoomDatabase() {

    abstract fun parkingReservationDao():
            ParkingReservationDao
}
