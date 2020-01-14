package com.example.weatherapp.api.model

import com.google.gson.annotations.SerializedName


data class JsonFile(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("country")
    val country: String

)