package com.habitatplus.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(
        report: ReportEntity
    )

    @Query(
        "SELECT * FROM reports ORDER BY id DESC"
    )
    fun getAllReports(): Flow<List<ReportEntity>>

    @Query(
        "SELECT * FROM reports WHERE id = :reportId LIMIT 1"
    )
    suspend fun getReportById(
        reportId: String
    ): ReportEntity?

    @Query(
        "DELETE FROM reports"
    )
    suspend fun deleteAll()
}
