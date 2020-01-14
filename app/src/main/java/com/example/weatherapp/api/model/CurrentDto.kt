package com.example.weatherapp.api.model

import com.google.gson.annotations.SerializedName

data class CurrentDto(

    @SerializedName("time")
    var time: String,

    @SerializedName("tempinside")
    var tempinside: Double,

    @SerializedName("tempair")
    var tempair: Double,

    @SerializedName("humidity")
    var humidity: Double,

    @SerializedName("pressure")
    var pressure: Double,

    @SerializedName("rain")
    var rain: Boolean,

    @SerializedName("raingauge")
    var raingauge: Double,

    @SerializedName("wind")
    var wind: Double
)