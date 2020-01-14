package com.example.weatherapp.api.model

import com.google.gson.annotations.SerializedName

data class CurrentSummaryDto(

    @SerializedName("tempinside")
    var tempinside: String,

    @SerializedName("min")
    var min: Double,

    @SerializedName("max")
    var max: Double,

    @SerializedName("tempair")
    var tempair: String,

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