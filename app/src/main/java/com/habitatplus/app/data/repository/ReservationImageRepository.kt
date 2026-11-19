package com.habitatplus.app.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class ReservationImageRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    private fun uid() = auth.currentUser?.uid ?: error("Debes iniciar sesión.")

    companion object {
        fun encode(bitmap: Bitmap): String {
            val scale = minOf(1f, 640f / maxOf(bitmap.width, bitmap.height))
            val resized = Bitmap.createScaledBitmap(bitmap,
                (bitmap.width * scale).toInt().coerceAtLeast(1),
                (bitmap.height * scale).toInt().coerceAtLeast(1), true)
            var quality = 65
            var bytes: ByteArray
            do {
                val stream = ByteArrayOutputStream()
                resized.compress(Bitmap.CompressFormat.JPEG, quality, stream)
                bytes = stream.toByteArray()
                quality -= 10
            } while (bytes.size > 95_000 && quality >= 25)
            require(bytes.size <= 130_000) { "La fotografía es demasiado grande." }
            return Base64.encodeToString(bytes, Base64.NO_WRAP)
        }
        fun decode(base64: String): Bitmap? = try {
            val bytes = Base64.decode(base64, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (_: Exception) { null }
    }

    suspend fun encodeUri(context: Context, uri: String): String = withContext(Dispatchers.IO) {
        val bitmap = context.contentResolver.openInputStream(Uri.parse(uri))?.use {
            BitmapFactory.decodeStream(it)
        } ?: error("No se pudo leer la fotografía.")
        encode(bitmap)
    }

    suspend fun save(type: String, id: String, encoded: String) {
        require(type == "report" || type == "parking")
        val owner = uid()
        val collection = if (type == "report") "reportImages" else "parkingImages"
        db.collection(collection).document(id).set(mapOf(
            "userId" to owner, "imageBase64" to encoded
        )).await()
    }

    suspend fun load(type: String, id: String): Bitmap? {
        require(type == "report" || type == "parking")
        val collection = if (type == "report") "reportImages" else "parkingImages"
        val data = db.collection(collection).document(id).get().await()
        return data.getString("imageBase64")?.let(::decode)
    }
}
