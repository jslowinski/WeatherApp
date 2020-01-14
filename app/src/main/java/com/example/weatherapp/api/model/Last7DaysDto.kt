package com.example.weatherapp.api.model

import com.google.gson.annotations.SerializedName

data class Last7DaysDto(

    @SerializedName("date")
    var date: String,

    @SerializedName("tempinside")
    var tempinside: String,

    @SerializedName("tempair")
    var tempair: String,

    @SerializedName("tempairnight")
    var tempairnight: String,

    @SerializedName("tempairday")
    var tempairday: String,

    @SerializedName("humidity")
    var humidity: String,

    @SerializedName("pressure")
    var pressure: String,

    @SerializedName("raingauge")
    var raingauge: String,

    @SerializedName("sum")
    var sum: Double,

    @SerializedName("wind")
    var wind: String
)