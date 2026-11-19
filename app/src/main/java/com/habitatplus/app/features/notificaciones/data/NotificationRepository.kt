package com.habitatplus.app.features.notificaciones.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.habitatplus.app.features.notificaciones.model.NotificationModel
import com.habitatplus.app.features.notificaciones.model.NotificationType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

private val Context.notificationDataStore by preferencesDataStore(
    name = "habitat_notifications"
)

class NotificationRepository(
    context: Context
) {

    private val dataStore =
        context.applicationContext.notificationDataStore

    private fun keyForUser(userId: String) =
        stringPreferencesKey("notifications_${userId}")

    fun getNotifications(
        userId: String
    ): Flow<List<NotificationModel>> {

        require(userId.isNotBlank()) {
            "El usuario no puede estar vacio."
        }

        val key = keyForUser(userId)

        return dataStore.data.map { preferences ->
            val json = preferences[key] ?: "[]"

            decodeNotifications(json)
                .filter { it.userId == userId }
                .sortedByDescending { it.createdAt }
        }
    }

    suspend fun saveNotification(
        notification: NotificationModel
    ) {
        require(notification.isValid()) {
            "La notificacion contiene datos invalidos."
        }

        val key = keyForUser(notification.userId)

        dataStore.edit { preferences ->
            val current = decodeNotifications(
                preferences[key] ?: "[]"
            )

            val updated = current.filterNot {
                it.id == notification.id
            } + notification

            preferences[key] = encodeNotifications(updated)
        }
    }

    suspend fun createNotification(
        userId: String,
        type: NotificationType,
        title: String,
        message: String,
        relatedId: String? = null
    ): NotificationModel {

        val notification = NotificationModel(
            id = UUID.randomUUID().toString(),
            userId = userId,
            type = type,
            title = title,
            message = message,
            relatedId = relatedId
        )

        saveNotification(notification)

        return notification
    }

    suspend fun markAsRead(
        userId: String,
        notificationId: String
    ) {
        require(userId.isNotBlank()) {
            "El usuario no puede estar vacio."
        }

        val key = keyForUser(userId)

        dataStore.edit { preferences ->
            val current = decodeNotifications(
                preferences[key] ?: "[]"
            )

            val updated = current.map { notification ->
                if (
                    notification.id == notificationId &&
                    notification.userId == userId
                ) {
                    notification.markAsRead()
                } else {
                    notification
                }
            }

            preferences[key] = encodeNotifications(updated)
        }
    }

    suspend fun markAllAsRead(
        userId: String
    ) {
        require(userId.isNotBlank()) {
            "El usuario no puede estar vacio."
        }

        val key = keyForUser(userId)

        dataStore.edit { preferences ->
            val current = decodeNotifications(
                preferences[key] ?: "[]"
            )

            val updated = current.map {
                if (it.userId == userId) {
                    it.markAsRead()
                } else {
                    it
                }
            }

            preferences[key] = encodeNotifications(updated)
        }
    }

    suspend fun deleteNotification(
        userId: String,
        notificationId: String
    ) {
        require(userId.isNotBlank()) {
            "El usuario no puede estar vacio."
        }

        val key = keyForUser(userId)

        dataStore.edit { preferences ->
            val current = decodeNotifications(
                preferences[key] ?: "[]"
            )

            val updated = current.filterNot {
                it.id == notificationId &&
                        it.userId == userId
            }

            preferences[key] = encodeNotifications(updated)
        }
    }

    private fun encodeNotifications(
        notifications: List<NotificationModel>
    ): String {

        val array = JSONArray()

        notifications.forEach { notification ->
            val item = JSONObject().apply {
                put("id", notification.id)
                put("userId", notification.userId)
                put("type", notification.type.name)
                put("title", notification.title)
                put("message", notification.message)
                put("createdAt", notification.createdAt)
                put("isRead", notification.isRead)

                notification.relatedId?.let {
                    put("relatedId", it)
                }
            }

            array.put(item)
        }

        return array.toString()
    }

    private fun decodeNotifications(
        json: String
    ): List<NotificationModel> {

        return try {
            val array = JSONArray(json)

            buildList {
                for (index in 0 until array.length()) {
                    val item = array.getJSONObject(index)

                    val type = runCatching {
                        NotificationType.valueOf(
                            item.getString("type")
                        )
                    }.getOrNull() ?: continue

                    val notification = NotificationModel(
                        id = item.optString("id"),
                        userId = item.optString("userId"),
                        type = type,
                        title = item.optString("title"),
                        message = item.optString("message"),
                        createdAt = item.optLong(
                            "createdAt",
                            0L
                        ),
                        isRead = item.optBoolean(
                            "isRead",
                            false
                        ),
                        relatedId = if (
                            item.has("relatedId") &&
                            !item.isNull("relatedId")
                        ) {
                            item.getString("relatedId")
                        } else {
                            null
                        }
                    )

                    if (notification.isValid()) {
                        add(notification)
                    }
                }
            }

        } catch (exception: Exception) {
            emptyList()
        }
    }
}
