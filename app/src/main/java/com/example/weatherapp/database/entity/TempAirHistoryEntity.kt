package com.example.weatherapp.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tempairhistory")
data class TempAirHistoryEntity(

    @ColumnInfo(name = "time")
    var time: String,

    @ColumnInfo(name = "tempair")
    var tempair: Double
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0
}
