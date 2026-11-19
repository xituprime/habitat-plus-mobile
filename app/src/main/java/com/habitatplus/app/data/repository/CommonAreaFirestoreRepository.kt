package com.habitatplus.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

data class CloudCommonAreaReservation(
    val id: String = "",
    val userId: String = "",
    val areaId: String = "",
    val date: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val status: String = "ACTIVE"
)

data class CloudReservationSnapshot(
    val reservations: List<CloudCommonAreaReservation> = emptyList(),
    val isFromCache: Boolean = true
)

class CommonAreaFirestoreRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    companion object {
        const val ACTIVE = "ACTIVE"
        const val CANCELLED = "CANCELLED"
    }

    private val reservations get() = firestore.collection("commonAreaReservations")
    private val slots get() = firestore.collection("commonAreaSlots")

    private fun requireUid() = auth.currentUser?.uid
        ?: error("Debes iniciar sesión para reservar.")

    private fun slotId(area: String, date: String, start: String) =
        "${area}_${date}_${start.replace(":", "")}" 

    // Solo lee disponibilidad pública, nunca datos personales de otros residentes.
    fun observeReservations(): Flow<CloudReservationSnapshot> = callbackFlow {
        requireUid()
        val listener = slots.addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val occupied = snapshot.documents.mapNotNull { doc ->
                    val area = doc.getString("areaId") ?: return@mapNotNull null
                    val date = doc.getString("date") ?: return@mapNotNull null
                    val start = doc.getString("startTime") ?: return@mapNotNull null
                    CloudCommonAreaReservation(
                        id = doc.id, areaId = area, date = date,
                        startTime = start, endTime = doc.getString("endTime").orEmpty(),
                        status = ACTIVE
                    )
                }
                trySend(CloudReservationSnapshot(occupied, snapshot.metadata.isFromCache))
            }
        }
        awaitClose { listener.remove() }
    }

    suspend fun reserve(areaId: String, date: String, startTime: String, endTime: String): String {
        val uid = requireUid()
        require(Regex("[a-z0-9-]{3,40}").matches(areaId))
        require(Regex("\\d{4}-\\d{2}-\\d{2}").matches(date))
        require((startTime to endTime) in setOf(
            "09:00" to "10:00", "10:00" to "11:00", "11:00" to "12:00",
            "12:00" to "13:00", "18:00" to "19:00"
        ))
        val id = slotId(areaId, date, startTime)
        val slot = slots.document(id)
        val reservation = reservations.document(id)
        firestore.runTransaction { tx ->
            val occupied = tx.get(slot)
            if (occupied.exists()) {
                throw IllegalStateException("Este horario ya está reservado.")
            }
            tx.set(reservation, CloudCommonAreaReservation(
                id = id, userId = uid, areaId = areaId, date = date,
                startTime = startTime, endTime = endTime, status = ACTIVE
            ))
            tx.set(slot, mapOf(
                "reservationId" to id, "areaId" to areaId,
                "date" to date, "startTime" to startTime, "endTime" to endTime
            ))
        }.await()
        return id
    }
}
