package com.habitatplus.app.data.local

import android.content.Context
import androidx.room.Room

object DatabaseProvider {

    @Volatile
    private var INSTANCE: HabitatDatabase? = null

    fun getDatabase(context: Context): HabitatDatabase {
        return INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                HabitatDatabase::class.java,
                "habitat_plus_database"
            ).build().also {
                INSTANCE = it
            }
        }
    }
}
