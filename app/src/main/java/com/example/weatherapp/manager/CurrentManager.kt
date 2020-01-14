package com.example.weatherapp.manager

import com.example.weatherapp.api.ApiClient
import com.example.weatherapp.api.model.CurrentDto
import com.example.weatherapp.database.AndroidDatabase
import com.example.weatherapp.database.entity.CurrentEntity
import com.example.weatherapp.database.entity.CurrentSummaryEntity
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kotlin.math.ln

class CurrentManager{

    private fun calculateDewPoint(tempair: Double, humidity: Double): Double {
        val variable = (ln(humidity/100) + ((17.27 * tempair) / (237.3 + tempair))) / 17.27
        return (237.3 * variable) / (1 - variable)
    }

    //region API

    private val apiService by lazy {
        ApiClient.create()
    }

    fun downloadCurrent(url: String): Completable =
        apiService
            .fetchCurrent(url)
            .flatMapCompletable {
                saveCurrent(
                    it.time,
                    it.tempinside,
                    it.tempair,
                    it.humidity,
                    it.pressure,
                    it.rain,
                    it.raingauge,
                    calculateDewPoint(it.tempair, it.humidity),
                    it.wind
                    )
            }
            .subscribeOn(Schedulers.io())

    fun downloadCurrentSummary(url: String): Completable =
        apiService
            .fetchCurrentSummary(url)
            .flatMapCompletable {
                saveCurrentSummary(
                    it.tempinside,
                    it.min,
                    it.max,
                    it.tempair,
                    it.humidity,
                    it.pressure,
                    it.raingauge,
                    it.sum,
                    it.wind
                )
            }
            .subscribeOn(Schedulers.io())

    //endregion

    //region Database

    private val database by lazy {
        AndroidDatabase.database
    }

    private fun saveCurrent(
        time: String,
        tempinside: Double,
        tempair: Double,
        humidity: Double,
        pressure: Double,
        rain: Boolean,
        raingauge: Double,
        dewpoint: Double,
        wind: Double) =
        Completable.fromAction {
            val entities = CurrentEntity(
                    time, tempinside, tempair, humidity, pressure, rain, raingauge, dewpoint, wind
                )
            database.currentDao().removeAndInsert(entities)
        }.subscribeOn(Schedulers.io())

    private fun saveCurrentSummary(
        tempinside: String,
        min: Double,
        max: Double,
        tempair: String,
        humidity: String,
        pressure: String,
        raingauge: String,
        sum: Double,
        wind: String) =
        Completable.fromAction{
            val entities = CurrentSummaryEntity(
                tempinside, min, max, tempair, humidity, pressure, raingauge, sum, wind
            )
            database.currentSummaryDao().removeAndInsert(entities)
        }.subscribeOn(Schedulers.io())

    fun getCurrent(): Maybe<CurrentEntity> =
        database
            .currentDao()
            .getCurrent()
            .subscribeOn(Schedulers.io())

    fun getCurrentSummary(): Maybe<CurrentSummaryEntity> =
        database
            .currentSummaryDao()
            .getCurrentSummary()
            .subscribeOn(Schedulers.io())

    //endregion
}