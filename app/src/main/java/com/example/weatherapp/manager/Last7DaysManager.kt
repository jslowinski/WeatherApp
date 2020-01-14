package com.example.weatherapp.manager

import com.example.weatherapp.api.ApiClient
import com.example.weatherapp.api.model.Last7DaysDto
import com.example.weatherapp.database.AndroidDatabase
import com.example.weatherapp.database.entity.Last7DaysEntity
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

class Last7DaysManager {

    //region API

    private val apiService by lazy {
        ApiClient.create()
    }

    fun downloadLast7Days(url: String): Completable =
        apiService
            .fetchLast7Days(url)
            .flatMapCompletable {
                saveLast7Days(
                    it.list
                )
            }
            .subscribeOn(Schedulers.io())

    //endregion

    //region Database

    private val database by lazy {
        AndroidDatabase.database
    }

    private fun saveLast7Days(last7DaysDto: List<Last7DaysDto>) =
        Completable.fromAction{
            val entities = last7DaysDto.map {
                Last7DaysEntity(
                    it.date,
                    it.tempinside,
                    it.tempair,
                    it.tempairnight,
                    it.tempairday,
                    it.humidity,
                    it.pressure,
                    it.raingauge,
                    it.sum,
                    it.wind
                )
            }
            database.last7DaysDao().removeAndInsert(entities)
        }.subscribeOn(Schedulers.io())

    fun getLast7Days() : Maybe<List<Last7DaysEntity>> =
        database
            .last7DaysDao()
            .getLast7days()
            .subscribeOn(Schedulers.io())
}