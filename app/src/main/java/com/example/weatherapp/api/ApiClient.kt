package com.example.weatherapp.api

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient {

    companion object {

        //region MyApi

        private const val BASE_URL = "https://stacja-pogodowa.herokuapp.com/"

        private val retrofit by lazy {
            Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
        }

        fun create() : ApiService{
            return retrofit.create(ApiService::class.java)
        }

        //endregion

        //region OpenWeatherApi

        private const val BASE_URL_OWA = "https://api.openweathermap.org/data/2.5/"

        private val retrofitOWA by lazy {
            Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL_OWA)
                .build()
        }

        fun createOWA() : ApiService{
            return  retrofitOWA.create(ApiService::class.java)
        }

        //endregion
    }
}