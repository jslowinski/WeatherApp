package com.example.weatherapp.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "current")

data class CurrentEntity(

    @ColumnInfo(name = "time")
    var time: String,

    @ColumnInfo(name = "tempinside")
    var tempinside: Double,

    @ColumnInfo(name = "tempair")
    var tempair: Double,

    @ColumnInfo(name = "humidity")
    var humidity: Double,

    @ColumnInfo(name = "pressure")
    var pressure: Double,

    @ColumnInfo(name = "rain")
    var rain: Boolean,

    @ColumnInfo(name = "raingauge")
    var raingauge: Double,

    @ColumnInfo(name = "dewpoint")
    var dewpoint: Double,

    @ColumnInfo(name = "wind")
    var wind: Double
)
{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0
}