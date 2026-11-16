package com.habitatplus.app.data.repository

import com.habitatplus.app.data.local.CommonAreaReservationDao
import com.habitatplus.app.data.local.CommonAreaReservationEntity
import kotlinx.coroutines.flow.Flow

class CommonAreaReservationRepository(
    private val reservationDao: CommonAreaReservationDao
) {

    val reservations: Flow<List<CommonAreaReservationEntity>> =
        reservationDao.getAllReservations()

    suspend fun saveReservation(
        reservation: CommonAreaReservationEntity
    ) {
        reservationDao.insert(reservation)
    }

    fun getReservationsForDate(
        areaId: String,
        date: String
    ): Flow<List<CommonAreaReservationEntity>> {
        return reservationDao.getReservationsForDate(
            areaId = areaId,
            date = date
        )
    }

    suspend fun isReserved(
        areaId: String,
        date: String,
        startTime: String
    ): Boolean {
        return reservationDao.countReservations(
            areaId = areaId,
            date = date,
            startTime = startTime
        ) > 0
    }
}
