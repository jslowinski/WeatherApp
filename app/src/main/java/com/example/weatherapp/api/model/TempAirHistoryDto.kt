package com.example.weatherapp.api.model

import com.google.gson.annotations.SerializedName

data class TempAirHistoryDto(

    @SerializedName("time")
    var time : String,

    @SerializedName("tempair")
    var tempair : Double
)