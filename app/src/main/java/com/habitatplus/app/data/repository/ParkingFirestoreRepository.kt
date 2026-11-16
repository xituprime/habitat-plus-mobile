package com.habitatplus.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class CloudParkingReservation(
    val id: String = "",
    val userId: String = "",
    val parkingId: String = "",
    val visitorName: String = "",
    val licensePlate: String = "",
    val vehicleBrand: String = "",
    val vehicleColor: String = "",
    val apartment: String = "",
    val date: String = "",
    val entryTime: String = "",
    val exitTime: String = "",
    val status: String = "Activa"
)

class ParkingFirestoreRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private fun currentUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException(
                "Debes iniciar sesión."
            )
    }

    suspend fun createReservation(
        reservation: CloudParkingReservation
    ): String {
        val userId = currentUserId()

        val document = db.collection("parkingReservations")
            .document()

        val savedReservation = reservation.copy(
            id = document.id,
            userId = userId,
            status = "Activa"
        )

        document.set(savedReservation).await()

        return document.id
    }

    suspend fun getMyReservations():
            List<CloudParkingReservation> {

        val userId = currentUserId()

        return db.collection("parkingReservations")
            .whereEqualTo("userId", userId)
            .get()
            .await()
            .toObjects(CloudParkingReservation::class.java)
    }

    suspend fun cancelReservation(
        reservationId: String
    ) {
        val userId = currentUserId()

        val document = db.collection("parkingReservations")
            .document(reservationId)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(document)

            if (!snapshot.exists()) {
                throw IllegalStateException(
                    "La reserva no existe."
                )
            }

            if (snapshot.getString("userId") != userId) {
                throw IllegalStateException(
                    "No puedes cancelar esta reserva."
                )
            }

            if (snapshot.getString("status") != "Activa") {
                throw IllegalStateException(
                    "La reserva ya no está activa."
                )
            }

            transaction.update(
                document,
                "status",
                "Cancelada"
            )
        }.await()
    }
}
