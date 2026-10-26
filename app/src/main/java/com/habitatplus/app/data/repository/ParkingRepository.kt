package com.habitatplus.app.data.repository

import com.habitatplus.app.data.local.ParkingReservationDao
import com.habitatplus.app.data.local.ParkingReservationEntity
import kotlinx.coroutines.flow.Flow

class ParkingRepository(
    private val dao: ParkingReservationDao
) {
    val reservations: Flow<List<ParkingReservationEntity>> =
        dao.getAllReservations()

    suspend fun saveReservation(
        reservation: ParkingReservationEntity
    ) {
        dao.insert(reservation)
    }

    suspend fun clearReservations() {
        dao.deleteAll()
    }
}
