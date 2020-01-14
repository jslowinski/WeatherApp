package com.example.weatherapp.api

import com.example.weatherapp.api.model.*
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiService {

    @GET
    fun fetchCurrent(@Url url: String): Single<CurrentDto>

    @GET
    fun fetchCurrentSummary(@Url url: String): Single<CurrentSummaryDto>

    @GET
    fun fetchHistory(@Url url: String): Single<List<TempAirHistoryDto>>

    @GET
    fun fetchOWAForecast(@Url url: String) : Single<OWR<List<OWRDto<OWRMainDto>>>>

    @GET
    fun fetchLast7Days(@Url url: String): Single<Result<List<Last7DaysDto>>>

    @GET
    fun fetchHourHistory(@Url url: String): Single<Result<List<HourHistoryDto>>>

    @GET
    fun fetchPickedInfo(@Url url: String): Single<CurrentDto>

    @GET("firstdate")
    fun fetchFirstDate() : Single<FirstDayDto>
}
