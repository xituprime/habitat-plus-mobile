
package com.habitatplus.app.features.admin.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Locale

data class PendingResident(
    val uid: String = "",
    val name: String = "",
    val lastName: String = "",
    val email: String = "",
    val requestedApartment: String = "",
    val residenceStatus: String = "pending"
)

class AdminResidentRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    private suspend fun requireAdmin() {
        val user = auth.currentUser
            ?: throw IllegalStateException(
                "Debes iniciar sesión."
            )

        val token = user.getIdToken(true).await()

        check(token.claims["admin"] == true) {
            "No tienes permisos de administrador."
        }
    }

    suspend fun getPendingResidents(): List<PendingResident> {
        requireAdmin()

        return db.collection("users")
            .whereEqualTo("residenceStatus", "pending")
            .get()
            .await()
            .documents
            .map { document ->
                PendingResident(
                    uid = document.id,
                    name = document.getString("name").orEmpty(),
                    lastName = document.getString("lastName").orEmpty(),
                    email = document.getString("email").orEmpty(),
                    requestedApartment =
                        document.getString("requestedApartment").orEmpty(),
                    residenceStatus =
                        document.getString("residenceStatus") ?: "pending"
                )
            }
    }

    suspend fun approveResidence(
        residentUid: String,
        apartment: String
    ) {
        requireAdmin()

        val unitId = apartment.trim()
            .uppercase(Locale.ROOT)

        require(Regex("[A-Z0-9-]{2,20}").matches(unitId)) {
            "Utiliza un identificador válido, por ejemplo A-203."
        }

        val userRef = db.collection("users")
            .document(residentUid)

        val unitRef = db.collection("residentialUnits")
            .document(unitId)

        db.runTransaction { transaction ->
            val userSnapshot = transaction.get(userRef)
            val unitSnapshot = transaction.get(unitRef)

            if (!userSnapshot.exists()) {
                throw IllegalStateException(
                    "El residente no existe."
                )
            }

            if (userSnapshot.getString("residenceStatus") != "pending") {
                throw IllegalStateException(
                    "La solicitud ya fue procesada."
                )
            }

            if (unitSnapshot.exists() &&
                unitSnapshot.getString("residentUid")
                    ?.isNotBlank() == true
            ) {
                throw IllegalStateException(
                    "La vivienda ya tiene un residente asignado."
                )
            }

            transaction.set(
                unitRef,
                mapOf(
                    "unitId" to unitId,
                    "residentUid" to residentUid,
                    "status" to "occupied"
                )
            )

            transaction.update(
                userRef,
                mapOf(
                    "apartment" to unitId,
                    "residenceStatus" to "approved"
                )
            )
        }.await()
    }

    suspend fun rejectResidence(residentUid: String) {
        requireAdmin()

        val userRef = db.collection("users")
            .document(residentUid)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(userRef)

            if (!snapshot.exists() ||
                snapshot.getString("residenceStatus") != "pending"
            ) {
                throw IllegalStateException(
                    "La solicitud ya fue procesada."
                )
            }

            transaction.update(
                userRef,
                mapOf(
                    "apartment" to "",
                    "residenceStatus" to "rejected"
                )
            )
        }.await()
    }
}
