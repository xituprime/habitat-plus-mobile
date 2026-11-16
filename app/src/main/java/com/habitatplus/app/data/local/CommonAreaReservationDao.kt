package com.habitatplus.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CommonAreaReservationDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(
        reservation: CommonAreaReservationEntity
    )

    @Query(
        "SELECT * FROM common_area_reservations ORDER BY date, startTime"
    )
    fun getAllReservations():
            Flow<List<CommonAreaReservationEntity>>

    @Query(
        """
        SELECT * FROM common_area_reservations
        WHERE areaId = :areaId
        AND date = :date
        ORDER BY startTime
        """
    )
    fun getReservationsForDate(
        areaId: String,
        date: String
    ): Flow<List<CommonAreaReservationEntity>>

    @Query(
        """
        SELECT COUNT(*) FROM common_area_reservations
        WHERE areaId = :areaId
        AND date = :date
        AND startTime = :startTime
        """
    )
    suspend fun countReservations(
        areaId: String,
        date: String,
        startTime: String
    ): Int
}
