package com.habitatplus.app.features.anuncios.model

data class Announcement(
    val id: String,
    val title: String,
    val category: String,
    val date: String,
    val summary: String,
    val body: String,
    val author: String = "Administración",
    val priority: Boolean = false,
    val publishedTime: String? = null,
    val serviceLabel: String? = null,
    val suspensionSchedule: String? = null,
    val suspensionDuration: String? = null
)

enum class AnnouncementFilter(val label: String) {
    ALL("Todos"),
    PRIORITY("Prioritarios"),
    MAINTENANCE("Mantenimiento"),
    INSTITUTIONAL("Institucional"),
    COMMUNITY("Convivencia")
}