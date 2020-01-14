package com.example.weatherapp.api.model

import com.google.gson.annotations.SerializedName

data class OWRDto<T>(

    @SerializedName("dt")
    var dt: Long,
    @SerializedName("main")
    var main: T
)