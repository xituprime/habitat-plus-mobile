package com.habitatplus.app.features.reportes.intent

import com.habitatplus.app.features.reportes.model.ReportCategory
import com.habitatplus.app.features.reportes.model.ReportStatus

sealed interface ReportIntent {

    data object LoadReports : ReportIntent

    data class SearchReports(
        val query: String
    ) : ReportIntent

    data class FilterByStatus(
        val status: ReportStatus?
    ) : ReportIntent

    data class SelectCategory(
        val category: ReportCategory
    ) : ReportIntent

    data class UpdateDescription(
        val description: String
    ) : ReportIntent

    data object SubmitReport : ReportIntent
}