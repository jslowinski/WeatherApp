package com.example.weatherapp.api.model

import com.google.gson.annotations.SerializedName

data class OWRMainDto(

    @SerializedName("temp")
    var temp: Double

)