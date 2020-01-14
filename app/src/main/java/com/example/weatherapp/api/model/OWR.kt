package com.example.weatherapp.api.model

data class OWR<T>(
    var cod: String,
    var message: Int,
    var cnt: Int,
    var list: T

)