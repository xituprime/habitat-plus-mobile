
package com.habitatplus.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportDao {

    // Guardar o actualizar un reporte.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(
        report: ReportEntity
    )

    // Consulta original.
    // Se conserva por compatibilidad con el repositorio.
    @Query(
        "SELECT * FROM reports ORDER BY id DESC"
    )
    fun getAllReports(): Flow<List<ReportEntity>>

    // Nueva consulta: reportes de un usuario específico.
    @Query(
        """
        SELECT * FROM reports
        WHERE userId = :userId
        ORDER BY id DESC
        """
    )
    fun getReportsByUser(
        userId: String
    ): Flow<List<ReportEntity>>

    // Buscar un reporte por su identificador.
    @Query(
        "SELECT * FROM reports WHERE id = :reportId LIMIT 1"
    )
    suspend fun getReportById(
        reportId: String
    ): ReportEntity?

    // Buscar un reporte verificando su propietario.
    @Query(
        """
        SELECT * FROM reports
        WHERE id = :reportId
        AND userId = :userId
        LIMIT 1
        """
    )
    suspend fun getReportByIdAndUser(
        reportId: String,
        userId: String
    ): ReportEntity?

    // Eliminar todos los reportes.
    // Se conserva por compatibilidad.
    @Query(
        "DELETE FROM reports"
    )
    suspend fun deleteAll()
}
