package com.example.weatherapp.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currentsummary")
data class CurrentSummaryEntity(

    @ColumnInfo(name = "tempinside")
    var tempinside: String,

    @ColumnInfo(name = "min")
    var min: Double,

    @ColumnInfo(name = "max")
    var max: Double,

    @ColumnInfo(name = "tempair")
    var tempair: String,

    @ColumnInfo(name = "humidity")
    var humidity: String,

    @ColumnInfo(name = "pressure")
    var pressure: String,

    @ColumnInfo(name = "raingauge")
    var raingauge: String,

    @ColumnInfo(name = "sum")
    var sum: Double,

    @ColumnInfo(name = "wind")
    var wind: String
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0
}