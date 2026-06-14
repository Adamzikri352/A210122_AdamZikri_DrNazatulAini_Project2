package com.example.a210122_nazatul_lab1

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// ------------------------------------------------------------------
// Lab 5 — Room Database (Singleton)
// ------------------------------------------------------------------


@Database(
    entities = [ApplianceEntity::class, BillGoalEntity::class],
    version = 1,
    exportSchema = false        // set true if you want schema JSON exports
)
abstract class SmartTenagaDatabase : RoomDatabase() {


    abstract fun applianceDao(): ApplianceDao
    abstract fun billGoalDao(): BillGoalDao

    companion object {
        @Volatile
        private var INSTANCE: SmartTenagaDatabase? = null


        fun getInstance(context: Context): SmartTenagaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SmartTenagaDatabase::class.java,
                    "smarttenaga.db"            // file name on device storage
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}