package com.habitatplus.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
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
    private val auth: FirebaseAuth =
        FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore =
        FirebaseFirestore.getInstance()
) {

    companion object {
        private const val COLLECTION =
            "commonAreaReservations"

        const val ACTIVE = "ACTIVE"
        const val CANCELLED = "CANCELLED"
    }

    private val collection
        get() = firestore.collection(COLLECTION)

    private fun requireUserId(): String =
        auth.currentUser?.uid
            ?: throw IllegalStateException(
                "Debes iniciar sesión para reservar."
            )

    private fun reservationId(
        areaId: String,
        date: String,
        startTime: String
    ): String {
        return "${areaId}_${date}_${startTime.replace(":", "")}"
    }

    fun observeReservations():
            Flow<CloudReservationSnapshot> = callbackFlow {

        requireUserId()

        val listener: ListenerRegistration =
            collection.addSnapshotListener(
                MetadataChanges.INCLUDE
            ) { snapshot, error ->

                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    trySend(
                        CloudReservationSnapshot(
                            reservations = snapshot.toObjects(
                                CloudCommonAreaReservation::class.java
                            ),
                            isFromCache =
                                snapshot.metadata.isFromCache
                        )
                    )
                }
            }

        awaitClose {
            listener.remove()
        }
    }

    suspend fun reserve(
        areaId: String,
        date: String,
        startTime: String,
        endTime: String
    ): String {

        val uid = requireUserId()

        require(areaId.isNotBlank())
        require(date.isNotBlank())
        require(startTime.isNotBlank())
        require(endTime.isNotBlank())

        val id = reservationId(
            areaId,
            date,
            startTime
        )

        val document = collection.document(id)

        firestore.runTransaction { transaction ->

            val snapshot = transaction.get(document)

            if (snapshot.exists()) {
                val status =
                    snapshot.getString("status") ?: ACTIVE

                if (status != CANCELLED) {
                    throw IllegalStateException(
                        "Este horario ya está reservado."
                    )
                }
            }

            val reservation =
                CloudCommonAreaReservation(
                    id = id,
                    userId = uid,
                    areaId = areaId,
                    date = date,
                    startTime = startTime,
                    endTime = endTime,
                    status = ACTIVE
                )

            transaction.set(
                document,
                reservation
            )

            id
        }.await()

        return id
    }
}
