package com.habitatplus.app.features.areascomunes.state

import com.habitatplus.app.features.areascomunes.model.CommonArea
import com.habitatplus.app.features.areascomunes.model.TimeSlot

data class CommonAreaState(
    val isLoading: Boolean = false,
    val areas: List<CommonArea> = emptyList(),

    val selectedAreaId: String? = null,
    val selectedDate: String? = null,
    val selectedTime: String? = null,

    val timeSlots: List<TimeSlot> = emptyList(),

    val isReservationConfirmed: Boolean = false
)