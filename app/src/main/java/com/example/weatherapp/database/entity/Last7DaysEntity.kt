package com.example.weatherapp.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "last7days")
data class Last7DaysEntity(

    @ColumnInfo(name = "date")
    var date: String,

    @ColumnInfo(name = "tempinside")
    var tempinside: String,

    @ColumnInfo(name = "tempair")
    var tempair: String,

    @ColumnInfo(name = "tempairnight")
    var tempairnight: String,

    @ColumnInfo(name = "tempairday")
    var tempairday: String,

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