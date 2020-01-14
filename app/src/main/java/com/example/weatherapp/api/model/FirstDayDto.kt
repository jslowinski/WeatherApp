package com.example.weatherapp.api.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class FirstDayDto(
    @SerializedName("date")
    var date: Date
)