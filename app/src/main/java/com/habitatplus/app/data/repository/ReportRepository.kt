
package com.habitatplus.app.data.repository

import com.habitatplus.app.data.local.ReportDao
import com.habitatplus.app.data.local.ReportEntity
import kotlinx.coroutines.flow.Flow

class ReportRepository(
    private val reportDao: ReportDao
) {

    // Consulta original.
    // Se conserva para compatibilidad con el codigo existente.
    val reports: Flow<List<ReportEntity>> =
        reportDao.getAllReports()

    // Nueva consulta: obtener solamente los reportes
    // asociados al usuario autenticado.
    fun getReportsByUser(
        userId: String
    ): Flow<List<ReportEntity>> {

        require(userId.isNotBlank()) {
            "El identificador del usuario no puede estar vacio."
        }

        return reportDao.getReportsByUser(userId)
    }

    // Guardar un reporte.
    // El propietario debe establecerse antes de guardar.
    suspend fun saveReport(
        report: ReportEntity
    ) {
        require(report.userId.isNotBlank()) {
            "No se puede guardar un reporte sin usuario."
        }

        reportDao.insert(report)
    }

    // Consulta original por identificador.
    // Se conserva por compatibilidad.
    suspend fun getReportById(
        reportId: String
    ): ReportEntity? {
        return reportDao.getReportById(reportId)
    }

    // Consultar un reporte verificando su propietario.
    suspend fun getReportByIdAndUser(
        reportId: String,
        userId: String
    ): ReportEntity? {

        require(userId.isNotBlank()) {
            "El identificador del usuario no puede estar vacio."
        }

        return reportDao.getReportByIdAndUser(
            reportId = reportId,
            userId = userId
        )
    }
}
