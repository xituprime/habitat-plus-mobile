package com.habitatplus.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reports")
data class ReportEntity(
    @PrimaryKey
    val id: String,
    val category: String,
    val description: String,
    val location: String,
    val date: String,
    val lastUpdate: String,
    val status: String,
    val hasPhoto: Boolean = false,
    val photoUri: String? = null
)
