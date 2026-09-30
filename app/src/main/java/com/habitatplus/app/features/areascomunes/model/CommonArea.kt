package com.habitatplus.app.features.areascomunes.model

data class CommonArea(
    val id: String,
    val name: String,
    val description: String,
    val locationType: String,
    val availabilityText: String,
    val features: List<String>,
    val status: CommonAreaStatus
)

enum class CommonAreaStatus {
    AVAILABLE,
    UNAVAILABLE,
    MAINTENANCE
}