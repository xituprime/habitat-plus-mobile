
package com.habitatplus.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

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

    private val reservations =
        db.collection("parkingReservations")

    private val slots =
        db.collection("parkingSlots")

    private val dateFormat =
        DateTimeFormatter.ISO_LOCAL_DATE

    private val timeFormat =
        DateTimeFormatter.ofPattern("HH:mm")

    private fun userId(): String =
        auth.currentUser?.uid
            ?: throw IllegalStateException(
                "Debes iniciar sesión."
            )

    private fun slotIds(
        parkingId: String,
        date: String,
        entry: String,
        exit: String
    ): List<String> {

        require(Regex("V-[0-9]{2}").matches(parkingId)) {
            "Parqueo inválido."
        }

        val parsedDate = try {
            LocalDate.parse(date, dateFormat)
        } catch (_: DateTimeParseException) {
            throw IllegalArgumentException(
                "Utiliza la fecha AAAA-MM-DD."
            )
        }

        require(!parsedDate.isBefore(LocalDate.now())) {
            "No puedes reservar fechas anteriores."
        }

        val start: LocalTime
        val end: LocalTime

        try {
            start = LocalTime.parse(entry, timeFormat)
            end = LocalTime.parse(exit, timeFormat)
        } catch (_: DateTimeParseException) {
            throw IllegalArgumentException(
                "Utiliza horarios HH:mm."
            )
        }

        require(start.minute % 30 == 0 &&
                end.minute % 30 == 0) {
            "Los horarios deben ser cada 30 minutos."
        }

        require(end.isAfter(start)) {
            "La salida debe ser posterior al ingreso."
        }

        val minutes =
            end.toSecondOfDay() / 60 -
                    start.toSecondOfDay() / 60

        require(minutes in 30..720) {
            "La duración debe ser de 30 minutos a 12 horas."
        }

        return (0 until minutes / 30).map { index ->
            val time = start.plusMinutes(index * 30L)
            val hour = "%02d%02d".format(
                java.util.Locale.US,
                time.hour,
                time.minute
            )
            "${parkingId}_${date}_$hour"
        }
    }

    suspend fun createReservation(
        reservation: CloudParkingReservation
    ): String {
        val uid = userId()

        val slotKeys = slotIds(
            reservation.parkingId,
            reservation.date,
            reservation.entryTime,
            reservation.exitTime
        )

        val doc = reservations.document()

        val saved = reservation.copy(
            id = doc.id,
            userId = uid,
            status = "Activa"
        )

        db.runTransaction { transaction ->

            // Todas las lecturas van antes de las escrituras.
            val slotRefs = slotKeys.map {
                slots.document(it)
            }

            val snapshots = slotRefs.map {
                transaction.get(it)
            }

            if (snapshots.any { it.exists() }) {
                throw IllegalStateException(
                    "El parqueo ya está reservado en ese horario."
                )
            }

            transaction.set(doc, saved)

            slotRefs.forEach { slotRef ->
                transaction.set(
                    slotRef,
                    mapOf(
                        "reservationId" to doc.id,
                        "userId" to uid,
                        "parkingId" to saved.parkingId,
                        "date" to saved.date
                    )
                )
            }

            doc.id
        }.await()

        return doc.id
    }

    suspend fun getMyReservations():
            List<CloudParkingReservation> {

        val uid = userId()

        return reservations
            .whereEqualTo("userId", uid)
            .get()
            .await()
            .toObjects(CloudParkingReservation::class.java)
            .sortedWith(
                compareByDescending<CloudParkingReservation> {
                    it.date
                }.thenByDescending { it.entryTime }
            )
    }

    suspend fun cancelReservation(
        reservationId: String
    ) {
        val uid = userId()
        val doc = reservations.document(reservationId)

        // Obtenemos los datos para calcular las franjas.
        val existing = doc.get().await()
            .toObject(CloudParkingReservation::class.java)
            ?: throw IllegalStateException(
                "Reserva no encontrada."
            )

        require(existing.userId == uid) {
            "No puedes cancelar esta reserva."
        }

        val slotKeys = slotIdsForCancellation(existing)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(doc)

            if (!snapshot.exists() ||
                snapshot.getString("userId") != uid ||
                snapshot.getString("status") != "Activa"
            ) {
                throw IllegalStateException(
                    "La reserva ya no está activa."
                )
            }

            val slotRefs = slotKeys.map {
                slots.document(it)
            }

            val slotSnapshots = slotRefs.map {
                transaction.get(it)
            }

            if (slotSnapshots.any {
                    !it.exists() ||
                            it.getString("reservationId") != reservationId
                }
            ) {
                throw IllegalStateException(
                    "Las franjas de esta reserva no coinciden."
                )
            }

            transaction.update(
                doc,
                "status",
                "Cancelada"
            )

            slotRefs.forEach {
                transaction.delete(it)
            }
        }.await()
    }

    private fun slotIdsForCancellation(
        reservation: CloudParkingReservation
    ): List<String> {
        // La cancelación debe poder realizarse incluso
        // si la fecha de la reserva ya pasó.
        val start = LocalTime.parse(
            reservation.entryTime,
            timeFormat
        )
        val end = LocalTime.parse(
            reservation.exitTime,
            timeFormat
        )

        val minutes =
            end.toSecondOfDay() / 60 -
                    start.toSecondOfDay() / 60

        require(minutes in 30..720 &&
                start.minute % 30 == 0 &&
                end.minute % 30 == 0) {
            "Horario de reserva inválido."
        }

        return (0 until minutes / 30).map { index ->
            val time = start.plusMinutes(index * 30L)
            val hour = "%02d%02d".format(
                java.util.Locale.US,
                time.hour,
                time.minute
            )
            "${reservation.parkingId}_${reservation.date}_$hour"
        }
    }
}
