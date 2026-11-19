
package com.habitatplus.app.features.admin.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.habitatplus.app.data.repository.CloudParkingReservation
import kotlinx.coroutines.tasks.await

data class AdminParkingRecord(
    val reservation: CloudParkingReservation,
    val residentName: String,
    val residentEmail: String
)

class AdminParkingRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private suspend fun requireAdmin(): String {
        val user = auth.currentUser
            ?: throw IllegalStateException("Debes iniciar sesión.")

        val token = user.getIdToken(true).await()

        check(
            auth.currentUser?.uid == user.uid &&
                    token.claims["admin"] == true
        ) {
            "No tienes permisos de administrador."
        }

        return user.uid
    }

    suspend fun getReservations(): List<AdminParkingRecord> {
        val adminUid = requireAdmin()

        val reservations = db.collection("parkingReservations")
            .get()
            .await()
            .toObjects(CloudParkingReservation::class.java)

        val users = mutableMapOf<String, Pair<String, String>>()

        for (uid in reservations.map { it.userId }
            .filter { it.isNotBlank() }
            .distinct()
        ) {
            check(auth.currentUser?.uid == adminUid) {
                "La sesión cambió."
            }

            val document = db.collection("users")
                .document(uid)
                .get()
                .await()

            val name = listOf(
                document.getString("name").orEmpty(),
                document.getString("lastName").orEmpty()
            ).filter { it.isNotBlank() }
                .joinToString(" ")
                .ifBlank { "Residente sin nombre" }

            val email = document.getString("email").orEmpty()

            users[uid] = name to email
        }

        check(auth.currentUser?.uid == adminUid) {
            "La sesión cambió."
        }

        return reservations.map { reservation ->
            val user = users[reservation.userId]

            AdminParkingRecord(
                reservation = reservation,
                residentName = user?.first ?: "Residente no encontrado",
                residentEmail = user?.second.orEmpty()
            )
        }.sortedWith(
            compareByDescending<AdminParkingRecord> {
                it.reservation.date
            }.thenByDescending {
                it.reservation.entryTime
            }
        )
    }
}
