package com.habitatplus.app.features.areascomunes.model

data class TimeSlot(
    val startTime: String,
    val endTime: String,
    val description: String,
    val status: TimeSlotStatus
)

enum class TimeSlotStatus {
    AVAILABLE,
    RESERVED
}