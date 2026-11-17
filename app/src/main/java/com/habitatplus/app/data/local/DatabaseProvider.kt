
package com.habitatplus.app.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseProvider {

    @Volatile
    private var INSTANCE: HabitatDatabase? = null

    // Migracion original: version 1 a 2.
    // Conservamos las tablas existentes.
    private val MIGRATION_1_2 = object : Migration(1, 2) {

        override fun migrate(db: SupportSQLiteDatabase) {

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `reports` (
                    `id` TEXT NOT NULL,
                    `category` TEXT NOT NULL,
                    `description` TEXT NOT NULL,
                    `location` TEXT NOT NULL,
                    `date` TEXT NOT NULL,
                    `lastUpdate` TEXT NOT NULL,
                    `status` TEXT NOT NULL,
                    `hasPhoto` INTEGER NOT NULL,
                    `photoUri` TEXT,
                    PRIMARY KEY(`id`)
                )
                """.trimIndent()
            )

            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `common_area_reservations` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `areaId` TEXT NOT NULL,
                    `date` TEXT NOT NULL,
                    `startTime` TEXT NOT NULL,
                    `endTime` TEXT NOT NULL
                )
                """.trimIndent()
            )

            db.execSQL(
                """
                CREATE UNIQUE INDEX IF NOT EXISTS
                `index_common_area_reservations_areaId_date_startTime`
                ON `common_area_reservations`
                (`areaId`, `date`, `startTime`)
                """.trimIndent()
            )
        }
    }

    // Nueva migracion: version 2 a 3.
    // Agrega el propietario a cada reporte.
    // No elimina los registros existentes.
    private val MIGRATION_2_3 = object : Migration(2, 3) {

        override fun migrate(db: SupportSQLiteDatabase) {

            db.execSQL(
                """
                ALTER TABLE `reports`
                ADD COLUMN `userId` TEXT NOT NULL DEFAULT ''
                """.trimIndent()
            )
        }
    }

    fun getDatabase(context: Context): HabitatDatabase {

        return INSTANCE ?: synchronized(this) {

            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                HabitatDatabase::class.java,
                "habitat_plus_database"
            )
                .addMigrations(
                    MIGRATION_1_2,
                    MIGRATION_2_3
                )
                .build()
                .also {
                    INSTANCE = it
                }
        }
    }
}
