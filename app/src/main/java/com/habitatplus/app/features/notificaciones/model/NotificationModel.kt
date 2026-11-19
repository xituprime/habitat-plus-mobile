
package com.habitatplus.app.features.notificaciones.model

enum class NotificationType {
    REPORT,
    PARKING,
    COMMON_AREA,
    ANNOUNCEMENT
}

data class NotificationModel(
    val id: String,
    val userId: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val relatedId: String? = null
) {

    val isUnread: Boolean
        get() = !isRead

    fun isValid(): Boolean {
        return id.isNotBlank() &&
                userId.isNotBlank() &&
                title.isNotBlank() &&
                message.isNotBlank() &&
                createdAt > 0L
    }

    fun markAsRead(): NotificationModel {
        return copy(isRead = true)
    }
}
