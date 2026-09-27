package com.habitatplus.app.features.reportes.model

data class Report(
    val id: String,
    val category: ReportCategory,
    val description: String,
    val location: String,
    val date: String,
    val lastUpdate: String,
    val status: ReportStatus,
    val hasPhoto: Boolean = false
)

enum class ReportCategory {
    LIGHTING,
    WATER,
    TRASH,
    NOISE,
    OTHER
}

enum class ReportStatus {
    PENDING,
    IN_PROGRESS,
    RESOLVED
}