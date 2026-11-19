package com.habitatplus.app.core.session

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserSessionRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore =
        FirebaseFirestore.getInstance()
) {

    suspend fun getCurrentUser(): ResidentUser {
        val firebaseUser = auth.currentUser
            ?: throw IllegalStateException(
                "No existe una sesión activa."
            )

        val snapshot = firestore.collection("users")
            .document(firebaseUser.uid)
            .get()
            .await()

        if (!snapshot.exists()) {
            throw IllegalStateException(
                "No se encontró el perfil del usuario."
            )
        }

        return ResidentUser(
            uid = firebaseUser.uid,
            name = snapshot.getString("name").orEmpty(),
            lastName = snapshot.getString("lastName").orEmpty(),
            email = firebaseUser.email.orEmpty(),
            phone = snapshot.getString("phone").orEmpty(),
            apartment = snapshot.getString("apartment").orEmpty(),
            requestedApartment =
                snapshot.getString("requestedApartment").orEmpty(),
            residenceStatus =
                snapshot.getString("residenceStatus") ?: "pending",
            role = snapshot.getString("role") ?: "resident",
            photoUrl = snapshot.getString("photoUrl").orEmpty()
        )
    }

    suspend fun updateContactInfo(
        phone: String
    ) {
        val uid = auth.currentUser?.uid
            ?: throw IllegalStateException(
                "Debes iniciar sesión."
            )

        firestore.collection("users")
            .document(uid)
            .update("phone", phone.trim())
            .await()
    }


    suspend fun updateProfile(
        fullName: String,
        phone: String
    ) {
        val uid = auth.currentUser?.uid
            ?: throw IllegalStateException(
                "Debes iniciar sesión."
            )

        val parts = fullName.trim()
            .split(Regex("\\s+"))

        val firstName = parts.first()
        val lastName = parts.drop(1).joinToString(" ")

        firestore.collection("users")
            .document(uid)
            .update(
                mapOf(
                    "name" to firstName,
                    "lastName" to lastName,
                    "phone" to phone.trim()
                )
            )
            .await()
    }

}
