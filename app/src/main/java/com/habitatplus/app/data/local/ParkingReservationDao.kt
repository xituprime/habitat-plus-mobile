package com.habitatplus.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ParkingReservationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(
        reservation: ParkingReservationEntity
    )

    @Query(
        "SELECT * FROM parking_reservations ORDER BY id DESC"
    )
    fun getAllReservations():
            Flow<List<ParkingReservationEntity>>

    @Query(
        "DELETE FROM parking_reservations"
    )
    suspend fun deleteAll()
}
