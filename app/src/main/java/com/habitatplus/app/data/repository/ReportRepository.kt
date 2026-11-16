package com.habitatplus.app.data.repository

import com.habitatplus.app.data.local.ReportDao
import com.habitatplus.app.data.local.ReportEntity
import kotlinx.coroutines.flow.Flow

class ReportRepository(
    private val reportDao: ReportDao
) {

    val reports: Flow<List<ReportEntity>> =
        reportDao.getAllReports()

    suspend fun saveReport(
        report: ReportEntity
    ) {
        reportDao.insert(report)
    }

    suspend fun getReportById(
        reportId: String
    ): ReportEntity? {
        return reportDao.getReportById(reportId)
    }
}
