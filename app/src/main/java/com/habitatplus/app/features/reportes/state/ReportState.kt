package com.habitatplus.app.features.reportes.state

import com.habitatplus.app.features.reportes.model.Report
import com.habitatplus.app.features.reportes.model.ReportCategory
import com.habitatplus.app.features.reportes.model.ReportStatus

data class ReportState(
    val isLoading: Boolean = false,
    val reports: List<Report> = emptyList(),
    val searchQuery: String = "",
    val selectedStatus: ReportStatus? = null,
    val selectedCategory: ReportCategory? = null,
    val description: String = "",
    val hasSelectedPhoto: Boolean = false,
    val isSubmitting: Boolean = false,
    val reportSubmitted: Boolean = false
)