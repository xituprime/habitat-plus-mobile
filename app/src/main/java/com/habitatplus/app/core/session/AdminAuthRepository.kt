package com.habitatplus.app.core.session

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AdminAuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    suspend fun isCurrentUserAdmin(): Boolean {
        val user = auth.currentUser ?: return false

        val token = user.getIdToken(true).await()

        if (auth.currentUser?.uid != user.uid) {
            return false
        }

        return token.claims["admin"] == true
    }
}
