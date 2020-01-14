package com.example.weatherapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherapp.database.dao.*
import com.example.weatherapp.database.entity.*

@Database(
    version = 7,
    exportSchema = false,
    entities = [
        CurrentEntity::class,
        OWTempAirEntity::class,
        TempAirHistoryEntity::class,
        CurrentSummaryEntity::class,
        Last7DaysEntity::class
    ]
)

abstract class  AndroidDatabase : RoomDatabase(){

    companion object{

        private const val DB_NAME = "cache_db"

        lateinit var database: AndroidDatabase
            private set

        fun initialize(context: Context) {
            database = Room
                .databaseBuilder(context, AndroidDatabase::class.java, DB_NAME)
                .fallbackToDestructiveMigration()
                .build()
        }
    }

    //region Dao

    abstract fun currentDao(): CurrentDao

    abstract fun oWTempAirDao(): OWTempAirDao

    abstract fun tempAirHistoryDao(): TempAirHistoryDao

    abstract fun currentSummaryDao(): CurrentSummaryDao

    abstract fun last7DaysDao(): Last7DaysDao

    //endregion
}