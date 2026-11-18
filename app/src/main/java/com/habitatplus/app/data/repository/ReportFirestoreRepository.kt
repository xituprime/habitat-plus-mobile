
package com.habitatplus.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

data class CloudReport(
    val id: String = "",
    val userId: String = "",
    val category: String = "",
    val description: String = "",
    val location: String = "",
    val date: String = "",
    val lastUpdate: String = "",
    val status: String = "PENDING",
    val hasPhoto: Boolean = false,
    val photoUrl: String? = null
)

class ReportFirestoreRepository(
    private val auth: FirebaseAuth =
        FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore =
        FirebaseFirestore.getInstance()
) {

    companion object {
        private const val COLLECTION_REPORTS = "reports"
    }

    private val reports
        get() = firestore.collection(COLLECTION_REPORTS)

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException(
                "Debes iniciar sesión para utilizar reportes."
            )
    }

    suspend fun createReport(report: CloudReport): String {
        val uid = requireUserId()

        require(report.category.isNotBlank()) {
            "Selecciona una categoría."
        }

        require(
            report.description.trim().isNotBlank() &&
                    report.description.length <= 500
        ) {
            "La descripción es obligatoria."
        }

        require(
            report.location.trim().isNotBlank() &&
                    report.location.length <= 150
        ) {
            "La ubicación es obligatoria."
        }

        val document = reports.document()

        document.set(
            report.copy(
                id = document.id,
                userId = uid,
                description = report.description.trim(),
                location = report.location.trim(),
                status = "PENDING"
            )
        ).await()

        return document.id
    }

    suspend fun getMyReports(): List<CloudReport> {
        val uid = requireUserId()

        return reports
            .whereEqualTo("userId", uid)
            .get()
            .await()
            .toObjects(CloudReport::class.java)
            .sortedWith(
                compareByDescending<CloudReport> { it.date }
                    .thenByDescending { it.id }
            )
    }

    fun observeMyReports(): Flow<List<CloudReport>> {
        val uid = requireUserId()

        return callbackFlow {
            val listener: ListenerRegistration = reports
                .whereEqualTo("userId", uid)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }

                    val result = snapshot
                        ?.toObjects(CloudReport::class.java)
                        .orEmpty()
                        .sortedWith(
                            compareByDescending<CloudReport> {
                                it.date
                            }.thenByDescending { it.id }
                        )

                    trySend(result)
                }

            awaitClose {
                listener.remove()
            }
        }
    }

    suspend fun getMyReportById(
        reportId: String
    ): CloudReport? {
        val uid = requireUserId()

        val snapshot = reports
            .document(reportId)
            .get()
            .await()

        val report = snapshot.toObject(
            CloudReport::class.java
        ) ?: return null

        return report.takeIf {
            it.userId == uid
        }
    }

    suspend fun updateMyReportDescription(
        reportId: String,
        description: String
    ) {
        val uid = requireUserId()

        require(
            description.trim().isNotBlank() &&
                    description.length <= 500
        ) {
            "La descripción debe tener entre 1 y 500 caracteres."
        }

        val document = reports.document(reportId)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(document)

            if (
                !snapshot.exists() ||
                snapshot.getString("userId") != uid
            ) {
                throw IllegalStateException(
                    "No puedes modificar este reporte."
                )
            }

            transaction.update(
                document,
                "description",
                description.trim()
            )
        }.await()
    }
}
