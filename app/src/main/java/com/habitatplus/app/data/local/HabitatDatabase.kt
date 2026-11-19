
package com.habitatplus.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        ParkingReservationEntity::class,
        ReportEntity::class,
        CommonAreaReservationEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class HabitatDatabase : RoomDatabase() {

    abstract fun parkingReservationDao():
            ParkingReservationDao

    abstract fun reportDao():
            ReportDao

    abstract fun commonAreaReservationDao():
            CommonAreaReservationDao
}
